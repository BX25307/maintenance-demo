package xyz.bx25.demo.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.bx25.demo.common.constants.RedisKeyConstants;
import xyz.bx25.demo.common.enums.ActionTypeEnum;
import xyz.bx25.demo.common.enums.OrderStatusEnum;
import xyz.bx25.demo.common.enums.UserTypeEnum;
import xyz.bx25.demo.common.enums.WorkStatusEnum;
import xyz.bx25.demo.common.exception.BusinessException;
import xyz.bx25.demo.common.util.UserContext;
import xyz.bx25.demo.mapper.DeviceInfoMapper;
import xyz.bx25.demo.mapper.RepairmanInfoMapper;
import xyz.bx25.demo.mapper.WorkOrderLogMapper;
import xyz.bx25.demo.mapper.WorkOrderMapper;
import xyz.bx25.demo.model.dto.OrderAssignDTO;
import xyz.bx25.demo.model.dto.order.OrderSubmitDTO;
import xyz.bx25.demo.model.entity.DeviceInfo;
import xyz.bx25.demo.model.entity.RepairmanInfo;
import xyz.bx25.demo.model.entity.WorkOrder;
import xyz.bx25.demo.model.entity.WorkOrderLog;
import xyz.bx25.demo.model.vo.OrderDetailVO;
import xyz.bx25.demo.model.vo.order.OrderListSimpleVO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WorkOrderService extends ServiceImpl<WorkOrderMapper, WorkOrder> {
    @Autowired
    private DeviceInfoMapper deviceInfoMapper;
    @Autowired
    private WorkOrderLogMapper workOrderLogMapper;
    @Autowired
    private RepairmanInfoMapper repairmanInfoMapper;
    @Autowired
    private List<IOrderStrategy> strategies;
    @Autowired
    private RedissonClient redissonClient;

    private Map<String, IOrderStrategy> strategyMap;
    @PostConstruct
    public void init(){
        strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        s -> s.getSupportedRole().getCode(),
                        Function.identity()
                ));
    }


    /**
     * 执行提交工单的业务逻辑（事务方法）
     */
    @Transactional(rollbackFor = Exception.class)
    public String submitOrder(OrderSubmitDTO dto) {
        String userId = UserContext.getUserId();
        String tenantId = UserContext.getTenantId();

        DeviceInfo deviceInfo = deviceInfoMapper.selectById(dto.getDeviceId());
        if(deviceInfo==null){
            throw new BusinessException("设备不存在");
        }
        if(!deviceInfo.getTenantId().equals(tenantId)){
            throw new BusinessException("非法操作:设备不属于当前租户");
        }
        String role = UserContext.getRoleKey();
        if(!role.equals(UserTypeEnum.USER.getCode())){
            throw new BusinessException("权限不足");
        }

        String orderId = UUID.randomUUID().toString().replace("-", "");
        String sn = "MAINTENANCE" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + ThreadLocalRandom.current().nextInt(100, 999);

        WorkOrder workOrder = WorkOrder.builder()
                .orderId(orderId)
                .orderSn(sn)
                .tenantId(tenantId)
                .deviceId(dto.getDeviceId())
                .deviceName(deviceInfo.getDeviceName())
                .orderStatus(OrderStatusEnum.PENDING.getCode())
                .addressDetail(dto.getAddressDetail())
                .faultDesc(dto.getFaultDesc())
                .reporterId(userId)
                .ownerId(deviceInfo.getUserId())
                .materialFee(BigDecimal.ZERO)
                .laborFee(BigDecimal.ZERO)
                .totalAmount(BigDecimal.ZERO)
                .platformIncome(BigDecimal.ZERO)
                .repairmanIncome(BigDecimal.ZERO)
                .platformRate(new BigDecimal("0.10"))
                .build();
        if(!CollectionUtils.isEmpty(dto.getFaultImages())){
            workOrder.setFaultImages(JSON.toJSONString(dto.getFaultImages()));
        }
        baseMapper.insert(workOrder);
        String desc= "创建工单";
        recordLog(orderId,userId,UserTypeEnum.USER,ActionTypeEnum.CREATE,desc);

        return orderId;
    }


    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(String orderId) {
        //todo 可能用户点击取消订单的同时刚好派单给维修员
        String role = UserContext.getRoleKey();
        //权限校验
        if(!role.equals(UserTypeEnum.USER.getCode())){
            throw new BusinessException("权限不足");
        }
        WorkOrder workOrder = baseMapper.selectById(orderId);
        if(workOrder==null){
            throw new BusinessException("工单不存在");
        }
        String userId = UserContext.getUserId();
        if(!workOrder.getReporterId().equals(userId)){
            throw new BusinessException("工单不属于当前用户");
        }
        if(!workOrder.getOrderStatus().equals(OrderStatusEnum.PENDING.getCode())){
            throw new BusinessException("工单状态不允许取消,请联系管理员处理");
        }
        LambdaUpdateWrapper<WorkOrder> wrapper= new LambdaUpdateWrapper<>();
        wrapper.eq(WorkOrder::getOrderId,orderId)
                .set(WorkOrder::getOrderStatus,OrderStatusEnum.CANCELLED.getCode());
        baseMapper.update(null,wrapper);

        String desc= "用户在待接单阶段取消工单";
        recordLog(orderId,userId,UserTypeEnum.USER,ActionTypeEnum.CANCEL,desc);
    }

    public Page<? extends OrderListSimpleVO> queryOrderList(Page<WorkOrder> page, Integer status) {
        String roleKey = UserContext.getRoleKey();
        IOrderStrategy strategy = strategyMap.get(roleKey);
        if(strategy==null&&UserTypeEnum.ADMIN.getCode().equals(roleKey)){
            strategy=strategyMap.get(UserTypeEnum.BOSS.getCode());
        }
        if(strategy==null){
            throw new BusinessException("当前角色不支持查询列表！！！");
        }
        LambdaQueryWrapper<WorkOrder> wrapper = strategy.buildQueryWrapper(
                UserContext.getUserId(),
                UserContext.getTenantId(),
                status
        );
        Page<WorkOrder> res = baseMapper.selectPage(page, wrapper);
        if(res==null){
            return null;
        }
        return strategy.convertPage(res);
    }

    public OrderDetailVO getOrderDetail(String orderId) {
        WorkOrder workOrder = baseMapper.selectById(orderId);
        if(workOrder==null) {
            throw new BusinessException("工单不存在");
        }
        String tenantId = UserContext.getTenantId();
        if(!workOrder.getTenantId().equals(tenantId)){
            throw new BusinessException("非法操作:工单不属于当前租户");
        }
        String roleKey = UserContext.getRoleKey();
        IOrderStrategy orderStrategy = strategyMap.get(roleKey);
        if(orderStrategy==null&&UserTypeEnum.ADMIN.getCode().equals(roleKey)){
            orderStrategy=strategyMap.get(UserTypeEnum.BOSS.getCode());
        }
        if(orderStrategy==null){
            throw new BusinessException("当前角色不支持查询详情！！！");
        }
        // 4. 【策略鉴权】
        if (!orderStrategy.hasPermission(workOrder, UserContext.getUserId())) {
            throw new BusinessException("权限不足：您不能查看该工单详情");
        }
        // 5. 【策略转换】
        return orderStrategy.convertDetail(workOrder);
    }
    /**
     * 记录操作日志 (私有方法，内部复用)
     */
    private void recordLog(String orderId, String operatorId, UserTypeEnum role, ActionTypeEnum action, String desc) {
        WorkOrderLog workOrderLog = WorkOrderLog.builder()
                .orderId(orderId)
                .operatorId(operatorId)
                .operatorRole(role.getCode())
                .actionType(action.getCode())
                .actionDesc(desc)
                .build();
        workOrderLogMapper.insert(workOrderLog);
    }

    @Transactional(rollbackFor = Exception.class)
    public void grabOrder(String orderId) {
        String repairmanId = UserContext.getUserId();
        String lockKey = RedisKeyConstants.getLockOrderKey(orderId);
        RLock lock = redissonClient.getLock(lockKey);
        try{
            if(lock.tryLock(0,10, TimeUnit.SECONDS)){
                // 2. 校验工单状态 (Double Check)
                WorkOrder order = baseMapper.selectById(orderId);
                if (order == null) throw new BusinessException("工单不存在");
                if (OrderStatusEnum.PENDING.getCode()!=order.getOrderStatus()) {
                    throw new BusinessException("手慢了，该工单已被抢或已取消");
                }

                // 3. 校验维修工状态 (可选：如果规定忙碌时不能抢)
                RepairmanInfo repairman = repairmanInfoMapper.selectById(repairmanId);
                if (repairman.getWorkStatus() == 1) throw new BusinessException("您当前有进行中的工单，请先完工");

                // 4. 执行抢单 (CAS 乐观锁更新)
                // 只有当 status = 0 且 repairman_id 为空时才更新
                LambdaUpdateWrapper<WorkOrder> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(WorkOrder::getOrderId, orderId)
                        .eq(WorkOrder::getOrderStatus, OrderStatusEnum.PENDING.getCode()) // 核心防线
                        .set(WorkOrder::getRepairmanId, repairmanId)
                        .set(WorkOrder::getOrderStatus, OrderStatusEnum.DISPATCHED.getCode())
                        .set(WorkOrder::getDispatchTime, LocalDateTime.now());

                int rows = baseMapper.update(null, updateWrapper);
                if (rows == 0) {
                    throw new BusinessException("抢单失败，可能已被他人抢走");
                }

                // 5. 更新维修工状态为忙碌 (BUSY)
                updateRepairmanStatus(repairmanId, WorkStatusEnum.BUSY.getCode());

                // 6. 记录日志
                recordLog(orderId, repairmanId, UserTypeEnum.REPAIRMAN, ActionTypeEnum.ASSIGN, "维修工主动抢单");
            }else{
                throw new BusinessException("抢单人数过多，请重试！！！");
            }
        }catch (InterruptedException e){
            throw new BusinessException("系统异常");
        }finally{
            if(lock.isLocked()&&lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }
    // 辅助方法：更新维修工状态
    private void updateRepairmanStatus(String repairmanId, int status) {
        RepairmanInfo info = new RepairmanInfo();
        info.setRepairmanId(repairmanId);
        info.setWorkStatus(status);
        repairmanInfoMapper.updateById(info);
    }


    @Transactional(rollbackFor = Exception.class)
    public void assignOrder(OrderAssignDTO dto) {
        String userId = UserContext.getUserId();

        // 1. 校验工单
        WorkOrder order = baseMapper.selectById(dto.getOrderId());
        if (order == null || OrderStatusEnum.PENDING.getCode()!=order.getOrderStatus()) {
            throw new BusinessException("工单状态异常，无法派单");
        }

        // 2. 校验维修工
        RepairmanInfo repairman = repairmanInfoMapper.selectById(dto.getRepairmanId());
        if (repairman == null) {
            throw new BusinessException("维修工不存在");
        }
        // 可选：校验维修工是否空闲
         if (WorkStatusEnum.BUSY.getCode() == repairman.getWorkStatus()) {
            throw new BusinessException("该维修工正忙，请指派他人");
         }

        // 3. 执行更新
        order.setRepairmanId(dto.getRepairmanId());
        order.setOrderStatus(OrderStatusEnum.DISPATCHED.getCode());
        order.setDispatchTime(LocalDateTime.now());
        baseMapper.updateById(order);

        // 4. 更新维修工状态
        updateRepairmanStatus(dto.getRepairmanId(), WorkStatusEnum.BUSY.getCode());

        // 5. 记录日志
        recordLog(dto.getOrderId(), userId, UserTypeEnum.ADMIN, ActionTypeEnum.ASSIGN,
                "管理员指派给：" + repairman.getRepairmanId());
    }

}

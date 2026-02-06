package xyz.bx25.demo.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.bx25.demo.common.constants.RedisKeyConstants;
import xyz.bx25.demo.common.enums.*;
import xyz.bx25.demo.common.exception.BusinessException;
import xyz.bx25.demo.common.util.UserContext;
import xyz.bx25.demo.mapper.*;
import xyz.bx25.demo.model.dto.order.*;
import xyz.bx25.demo.model.entity.*;
import xyz.bx25.demo.model.vo.order.OrderDetailVO;
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
    private SysUserMapper sysUserMapper;
    @Autowired
    private CapitalFlowMapper capitalFlowMapper;
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

    @Transactional(rollbackFor = Exception.class)
    public void finishRepair(OrderFinishDTO dto) {
        String userId = UserContext.getUserId();
        WorkOrder order = baseMapper.selectById(dto.getOrderId());
        if(order==null){
            throw new BusinessException("工单不存在");
        }
        if(!userId.equals(order.getRepairmanId())){
            throw new BusinessException("无权操作此工单!!!");
        }
        if(OrderStatusEnum.DISPATCHED.getCode()!=order.getOrderStatus()) {
            throw new BusinessException("工单状态异常，无法完成");
        }
        BigDecimal rate = order.getPlatformRate();
        if(rate==null) {
            rate = new BigDecimal("0.10");
        }
        //人工费
        BigDecimal laborFee = dto.getLaborFee()==null?new BigDecimal("50"):dto.getLaborFee();
        //材料费
        BigDecimal materialFee = dto.getMaterialFee();
        //平台分成
        BigDecimal platformIncome= laborFee.multiply(rate);
        //维修工收入
        BigDecimal repairmanIncome = materialFee.add(laborFee.subtract(platformIncome));
        //老板应付
        BigDecimal totalAmount = laborFee.add(materialFee);

        // 5. 更新工单信息
        order.setRepairResult(dto.getRepairResult());
        // 图片 List 转 JSON 存库
        order.setRepairImages(JSON.toJSONString(dto.getRepairImages()));
        order.setFeeImages(JSON.toJSONString(dto.getFeeImages()));

        // 填入金额
        order.setMaterialFee(materialFee);
        order.setLaborFee(laborFee);
        order.setPlatformIncome(platformIncome);
        order.setRepairmanIncome(repairmanIncome);
        order.setTotalAmount(totalAmount);

        // 变更状态
        order.setOrderStatus(OrderStatusEnum.PAYING.getCode()); // 3: 待支付
        order.setFinishTime(LocalDateTime.now());

        baseMapper.updateById(order);

        updateRepairmanStatus(userId,WorkStatusEnum.FREE.getCode());

        String desc= String.format("完工提交。总额:%.2f (材料:%.2f, 人工:%.2f)", totalAmount, materialFee, laborFee);
        recordLog(order.getOrderId(), userId, UserTypeEnum.REPAIRMAN, ActionTypeEnum.FINISH,desc);
    }
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(OrderPayDTO dto) {
        String bossId = UserContext.getUserId();
        String tenantId = UserContext.getTenantId();
        WorkOrder order = baseMapper.selectById(dto.getOrderId());
        if(order==null){
            throw new BusinessException("工单不存在");
        }
        if(OrderStatusEnum.PAYING.getCode()!=order.getOrderStatus()) {
            throw new BusinessException("工单状态异常，无法支付");
        }
        if(!tenantId.equals(order.getTenantId())||!bossId.equals(order.getOwnerId())) {
            throw new BusinessException("无权操作此工单!!!");
        }
        SysUser boss = sysUserMapper.selectById(bossId);
        BigDecimal totalAmount = order.getTotalAmount();
        if(boss.getBalance().compareTo(totalAmount)<0) {
            throw new BusinessException("余额不足，无法支付,先充值！！！");
        }
        boss.setBalance(boss.getBalance().subtract(totalAmount));
        sysUserMapper.updateById(boss);

        String repairmanId = order.getRepairmanId();
        if(StringUtils.isBlank(repairmanId)) {
            throw new BusinessException("无维修员,尚未分配!");
        }
        SysUser repairman = sysUserMapper.selectById(repairmanId);
        if(repairman==null) {
            throw new BusinessException("维修员不存在");
        }
        BigDecimal income = order.getRepairmanIncome();
        repairman.setBalance(repairman.getBalance().add(income));
        sysUserMapper.updateById(repairman);

        String tradeNo = "TRX" + System.currentTimeMillis();

        createCapitalFlow(tradeNo, order.getOrderId(), bossId, FlowTypeEnum.PAY, totalAmount, boss.getBalance(), "支付工单", tenantId);

        createCapitalFlow(tradeNo, order.getOrderId(), repairmanId, FlowTypeEnum.INCOME, income, repairman.getBalance(), "维修员收入", tenantId);
        createCapitalFlow(tradeNo, order.getOrderId(), "SYSTEM_PLATFORM", FlowTypeEnum.INCOME, order.getPlatformIncome(), null, "平台技术服务费", tenantId);
        LambdaUpdateWrapper<WorkOrder> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(WorkOrder::getOrderId, dto.getOrderId())
                .set(WorkOrder::getOrderStatus, OrderStatusEnum.FINISHED.getCode())
                .set(WorkOrder::getPayTime, LocalDateTime.now());
        baseMapper.update(null, wrapper);

        String desc="老板确认支付，订单完成。交易额：" + totalAmount;
        recordLog(order.getOrderId(), bossId, UserTypeEnum.BOSS, ActionTypeEnum.PAY,desc);
    }

    @Transactional(rollbackFor = Exception.class)
    public void appealOrder(OrderAppealDTO dto) {
        WorkOrder order = baseMapper.selectById(dto.getOrderId());
        if(order==null){
            throw new BusinessException("工单不存在");
        }
        if(OrderStatusEnum.FINISHED.getCode()!=order.getOrderStatus()) {
            throw new BusinessException("工单状态异常，无法申诉");
        }
        String bossId = UserContext.getUserId();
        String tenantId = UserContext.getTenantId();
        if(!tenantId.equals(order.getTenantId())||!bossId.equals(order.getOwnerId())) {
            throw new BusinessException("无权操作此工单!!!");
        }
        LambdaUpdateWrapper<WorkOrder> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(WorkOrder::getOrderId, dto.getOrderId())
                .set(WorkOrder::getOrderStatus, OrderStatusEnum.APPEAL.getCode())
                .set(WorkOrder::getAppealReason, dto.getAppealReason());
        baseMapper.update(null, wrapper);
        String desc="老板发起申诉，申诉原因：" + dto.getAppealReason();
        recordLog(order.getOrderId(), bossId, UserTypeEnum.BOSS, ActionTypeEnum.APPEAL,desc);
    }
    /**
     * 辅助方法：创建资金流水
     */
    private void createCapitalFlow(String tradeNo, String orderId, String accountId,
                                   FlowTypeEnum flowType, BigDecimal amount,
                                   BigDecimal balanceSnapshot, String remark, String tenantId) {
        CapitalFlow flow = new CapitalFlow();
        flow.setTradeNo(tradeNo);
        flow.setOrderId(orderId);
        flow.setAccountId(accountId);
        flow.setFlowType(flowType.name());
        flow.setAmount(amount);
        flow.setBalanceSnapshot(balanceSnapshot); // 记录变动后的余额快照
        flow.setRemark(remark);
        flow.setTenantId(tenantId);
        flow.setCreateTime(LocalDateTime.now());

        capitalFlowMapper.insert(flow);
    }

    @Transactional(rollbackFor = Exception.class)
    public void auditAdjust(OrderAdjustDTO dto) {
        String adminId = UserContext.getUserId();

        // 1. 查单
        WorkOrder order = baseMapper.selectById(dto.getOrderId());
        if (order == null) {
            throw new BusinessException("工单不存在");
        }

        // 2. 状态校验：只有 [申诉中] 的工单才能进行裁决
        if (OrderStatusEnum.APPEAL.getCode()!=order.getOrderStatus()) {
            throw new BusinessException("操作失败：工单不在申诉状态，无法调整");
        }

        // 3. 重新计算金额 (核心：逻辑必须与完工结算保持一致)
        // 3.1 获取费率 (沿用下单时的快照，如果没有则取默认值)
        BigDecimal rate = order.getPlatformRate();
        if (rate == null) {
            rate = new BigDecimal("0.10");
        }

        BigDecimal newLaborFee = dto.getLaborFee();
        BigDecimal newMaterialFee = dto.getMaterialFee();

        // 3.2 计算各项分润
        // 平台抽成 = 新人工费 * 费率
        BigDecimal platformIncome = newLaborFee.multiply(rate);
        // 总金额 = 新人工费 + 新材料费
        BigDecimal totalAmount = newLaborFee.add(newMaterialFee);
        // 维修工收入 = 总金额 - 平台抽成
        BigDecimal repairmanIncome = totalAmount.subtract(platformIncome);

        // 4. 执行更新
        LambdaUpdateWrapper<WorkOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(WorkOrder::getOrderId, order.getOrderId())
                // 更新金额字段
                .set(WorkOrder::getLaborFee, newLaborFee)
                .set(WorkOrder::getMaterialFee, newMaterialFee)
                .set(WorkOrder::getTotalAmount, totalAmount)
                .set(WorkOrder::getPlatformIncome, platformIncome)
                .set(WorkOrder::getRepairmanIncome, repairmanIncome)
                // 核心状态流转：回退到 3 (待支付)
                .set(WorkOrder::getOrderStatus, OrderStatusEnum.FINISHED.getCode())
                // 记录管理员的处理记录
                .set(WorkOrder::getAppealHandleLog, dto.getAuditRemark())
                .set(WorkOrder::getUpdateTime, LocalDateTime.now());

        baseMapper.update(null, updateWrapper);

        // 5. 记录操作日志
        recordLog(order.getOrderId(), adminId, UserTypeEnum.ADMIN, ActionTypeEnum.APPEAL,
                String.format("管理员改判金额。新总额:%.2f (含材料:%.2f, 人工:%.2f)，理由:%s",
                        totalAmount, newMaterialFee, newLaborFee, dto.getAuditRemark()));
    }

    @Transactional(rollbackFor = Exception.class)
    public void auditReject(OrderRejectDTO dto) {
        String adminId = UserContext.getUserId();

        // 1. 查单
        WorkOrder order = baseMapper.selectById(dto.getOrderId());
        if (order == null) {
            throw new BusinessException("工单不存在");
        }

        // 2. 状态校验：只有 [申诉中] 的工单才能进行裁决
        if (OrderStatusEnum.APPEAL.getCode()!=order.getOrderStatus()) {
            throw new BusinessException("操作失败：工单不在申诉状态，无法驳回");
        }

        // 3. 执行更新
        LambdaUpdateWrapper<WorkOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(WorkOrder::getOrderId, order.getOrderId())
                // 核心状态流转：回退到 3 (待支付)
                .set(WorkOrder::getOrderStatus, OrderStatusEnum.FINISHED.getCode())
                // 记录管理员的处理意见
                .set(WorkOrder::getAppealHandleLog, dto.getAuditRemark())
                .set(WorkOrder::getUpdateTime, LocalDateTime.now());

        baseMapper.update(null, updateWrapper);

        // 4. 记录操作日志
        recordLog(order.getOrderId(), adminId, UserTypeEnum.ADMIN, ActionTypeEnum.APPEAL,
                "管理员裁决：申诉无效，维持原判。理由：" + dto.getAuditRemark());
    }
}

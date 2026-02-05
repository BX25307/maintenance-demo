package xyz.bx25.demo.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.bx25.demo.common.enums.ActionTypeEnum;
import xyz.bx25.demo.common.enums.OrderStatusEnum;
import xyz.bx25.demo.common.enums.UserTypeEnum;
import xyz.bx25.demo.common.exception.BusinessException;
import xyz.bx25.demo.common.util.UserContext;
import xyz.bx25.demo.mapper.DeviceInfoMapper;
import xyz.bx25.demo.mapper.WorkOrderLogMapper;
import xyz.bx25.demo.mapper.WorkOrderMapper;
import xyz.bx25.demo.model.dto.order.OrderSubmitDTO;
import xyz.bx25.demo.model.entity.DeviceInfo;
import xyz.bx25.demo.model.entity.WorkOrder;
import xyz.bx25.demo.model.entity.WorkOrderLog;
import xyz.bx25.demo.model.vo.OrderDetailVO;
import xyz.bx25.demo.model.vo.order.OrderListSimpleVO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class WorkOrderService extends ServiceImpl<WorkOrderMapper, WorkOrder> {
    @Autowired
    private DeviceInfoMapper deviceInfoMapper;
    @Autowired
    private WorkOrderLogMapper workOrderLogMapper;


    @Transactional(rollbackFor = Exception.class)
    public String submitOrder(OrderSubmitDTO dto) {
        String userId = UserContext.getUserId();
        String tenantId = UserContext.getTenantId();

        DeviceInfo deviceInfo = deviceInfoMapper.selectById(dto.getDeviceId());
        if(deviceInfo==null){
            throw new RuntimeException("设备不存在");
        }
        if(!deviceInfo.getTenantId().equals(tenantId)){
            throw new RuntimeException("非法操作:设备不属于当前租户");
        }
        String orderId = UUID.randomUUID().toString().replace("-", "");
        String sn = "MAINTENANCE" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + ThreadLocalRandom.current().nextInt(100, 999);

        WorkOrder workOrder = WorkOrder.builder()
                .orderId(orderId)
                .orderSn(sn)
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

    public Page<OrderListSimpleVO> queryOrderList(Page page, Integer status) {


    }

    public OrderDetailVO getOrderDetail(String orderId) {

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
                .build();
        workOrderLogMapper.insert(workOrderLog);
    }
}

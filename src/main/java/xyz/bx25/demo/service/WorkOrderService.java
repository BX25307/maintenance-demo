package xyz.bx25.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import xyz.bx25.demo.common.enums.ActionTypeEnum;
import xyz.bx25.demo.common.enums.OrderStatusEnum;
import xyz.bx25.demo.common.enums.UserTypeEnum;
import xyz.bx25.demo.common.util.UserContext;
import xyz.bx25.demo.mapper.DeviceInfoMapper;
import xyz.bx25.demo.mapper.WorkOrderLogMapper;
import xyz.bx25.demo.mapper.WorkOrderMapper;
import xyz.bx25.demo.model.dto.OrderAssignDTO;
import xyz.bx25.demo.model.dto.OrderSubmitDTO;
import xyz.bx25.demo.model.entity.DeviceInfo;
import xyz.bx25.demo.model.entity.WorkOrder;
import xyz.bx25.demo.model.entity.WorkOrderLog;
import xyz.bx25.demo.model.vo.OrderDetailVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
            throw new RuntimeException("设备不属于当前租户");
        }
        WorkOrder order = WorkOrder.builder()
                .deviceId(dto.getDeviceId())
                .ownerId(deviceInfo.getUserId())
                .reporterId(userId)
                .faultDesc(dto.getFaultDesc())
                .faultImages(dto.getFaultImages())
                .orderStatus(OrderStatusEnum.PENDING.getCode())
                .tenantId(tenantId)
                .build();
        baseMapper.insert(order);
        String roleKey = UserContext.getRoleKey();
        recordLog(order.getOrderId(),userId,UserTypeEnum.getDescByCode(roleKey), ActionTypeEnum.CREATE, "创建工单");
        return order.getOrderId();
    }

    public void assignOrder(OrderAssignDTO dto) {
    }

    public void finishOrder(String orderId, String repairResult) {
    }

    public List<OrderDetailVO> queryOrderList(String queryUserId) {
        // 1. 获取当前用户信息
        String currentRole = UserContext.getRoleKey();
        String currentId = UserContext.getUserId();
        String tenantId = UserContext.getTenantId();

        LambdaQueryWrapper<WorkOrder> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(WorkOrder::getTenantId, tenantId).orderByDesc(WorkOrder::getCreateTime);
        if(UserTypeEnum.REPAIRMAN.getCode().equals(currentRole)){
            wrapper.eq(WorkOrder::getTenantId,tenantId);
        }else if(UserTypeEnum.USER.getCode().equals(currentRole)){
            wrapper.eq(WorkOrder::getReporterId,currentId);
        }else if (UserTypeEnum.BOSS.getCode().equals(currentRole)){
            wrapper.eq(WorkOrder::getOwnerId,currentId);
        }else if (UserTypeEnum.ADMIN.getCode().equals(currentRole)){
            //如果是管理员，可查看所有工单或指定工单(报修人)
            if(StringUtils.hasText(queryUserId)){
                wrapper.eq(WorkOrder::getReporterId,queryUserId);
            }
        }else{
            throw new RuntimeException("用户角色错误或无权限!!");
        }
        List<WorkOrder> list = this.list(wrapper);

        return list.stream()
                .map(this::convertVO)
                .collect(Collectors.toList());
    }

    public OrderDetailVO getOrderDetail(String orderId) {
        return null;
    }

    /**
     * 记录操作日志 (私有方法，内部复用)
     */
    private void recordLog(String orderId, String operatorId, String role, ActionTypeEnum action, String desc) {
        WorkOrderLog log = new WorkOrderLog();
        log.setOrderId(orderId);
        log.setOperatorId(operatorId);
        log.setOperatorRole(role);
        log.setActionType(action.getCode());
        log.setActionDesc(desc);
        log.setCreateTime(LocalDateTime.now());

        workOrderLogMapper.insert(log);
    }

    private OrderDetailVO convertVO(WorkOrder order) {
        return OrderDetailVO.builder()
                .orderId(order.getOrderId())
                .deviceId(order.getDeviceId())
                .faultDesc(order.getFaultDesc())
                .faultImages(order.getFaultImages())
                .reporterId(order.getReporterId())
                .createTime(order.getCreateTime())
                .orderStatus(order.getOrderStatus())
                .statusText(OrderStatusEnum.getDescByCode(order.getOrderStatus()))
                .repairmanId(order.getRepairmanId())
                .assignTime(order.getAssignTime())
                .repairResult(order.getRepairResult())
                .finishTime(order.getFinishTime())
                .build();
    }

}

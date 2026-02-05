package xyz.bx25.demo.service.strategy;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import xyz.bx25.demo.common.enums.OrderStatusEnum;
import xyz.bx25.demo.common.enums.UserTypeEnum;
import xyz.bx25.demo.model.entity.WorkOrder;
import xyz.bx25.demo.model.vo.order.OrderListSimpleVO;
import xyz.bx25.demo.model.vo.order.list.OrderListSimpleRepairmanVO;
import xyz.bx25.demo.service.AbstractOrderStrategy;

import java.util.Map;

@Component
public class RepairmanOrderStrategy extends AbstractOrderStrategy {

    @Override
    public UserTypeEnum getSupportedRole() {
        return UserTypeEnum.REPAIRMAN;
    }

    @Override
    public LambdaQueryWrapper<WorkOrder> buildQueryWrapper(String userId, String tenantId, Integer status) {
        // 规则：复用基础条件 + 指派给我的
        return createBaseWrapper(tenantId, status)
                .eq(WorkOrder::getRepairmanId, userId);
    }

    @Override
    public Page<? extends OrderListSimpleVO> convertPage(Page<WorkOrder> rawPage) {
        // 1. 查设备名 (复用父类方法)
        Map<String, String> deviceMap = getDeviceMap(rawPage.getRecords());

        // 2. 转换 VO
        return (Page<OrderListSimpleRepairmanVO>) rawPage.convert(order -> {
            OrderListSimpleRepairmanVO vo = new OrderListSimpleRepairmanVO();
            BeanUtils.copyProperties(order, vo); // 复制基础字段

            // 基础字段填充
            vo.setStatusText(OrderStatusEnum.getDescByCode(order.getOrderStatus()));
            vo.setDeviceName(deviceMap.getOrDefault(order.getDeviceId(), "未知设备"));

            // 【特有字段填充】维修工必须看地址和故障
            vo.setAddressDetail(order.getAddressDetail());
            vo.setFaultDesc(order.getFaultDesc());

            return vo;
        });
    }

    @Override
    public boolean hasPermission(WorkOrder order, String userId) {
        // 1. 指派给我的
        if (userId.equals(order.getRepairmanId())) {
            return true;
        }
        // 2. 待接单的 (抢单池里的单子，必须允许查看详情，否则不敢抢)
        return OrderStatusEnum.PENDING.getCode() == order.getOrderStatus();
    }
}
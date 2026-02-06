package xyz.bx25.demo.service.strategy;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import xyz.bx25.demo.common.enums.OrderStatusEnum;
import xyz.bx25.demo.common.enums.UserTypeEnum;
import xyz.bx25.demo.model.entity.WorkOrder;
import xyz.bx25.demo.model.vo.order.OrderDetailVO;
import xyz.bx25.demo.model.vo.order.OrderListSimpleVO;
import xyz.bx25.demo.service.AbstractOrderStrategy;

import java.util.Map;

@Component
public class UserOrderStrategy extends AbstractOrderStrategy {

    @Override
    public UserTypeEnum getSupportedRole() {
        return UserTypeEnum.USER;
    }

    @Override
    public LambdaQueryWrapper<WorkOrder> buildQueryWrapper(String userId, String tenantId, Integer status) {
        // 复用父类基础条件，追加 "查自己" 的条件
        return createBaseWrapper(tenantId, status)
                .eq(WorkOrder::getReporterId, userId);
    }

    @Override
    public Page<? extends OrderListSimpleVO> convertPage(Page<WorkOrder> rawPage) {
        // 复用父类方法查设备
        Map<String, String> deviceMap = getDeviceMap(rawPage.getRecords());

        return (Page<OrderListSimpleVO>) rawPage.convert(order -> {
            OrderListSimpleVO vo = new OrderListSimpleVO();
            BeanUtils.copyProperties(order, vo);

            vo.setStatusText(OrderStatusEnum.getDescByCode(order.getOrderStatus()));
            vo.setDeviceName(deviceMap.getOrDefault(order.getDeviceId(), "未知设备"));
            return vo;
        });
    }

    @Override
    public boolean hasPermission(WorkOrder order, String userId) {
        // 严格限制：只能看自己的
        return order.getReporterId().equals(userId);
    }

    @Override
    public OrderDetailVO convertDetail(WorkOrder order) {
        OrderDetailVO vo = super.convertDetail(order);
        // 用户不需要看具体的材料费、人工费，只看总价即可 (或者根据业务需求隐藏)
         vo.setLaborFee(null);
         vo.setMaterialFee(null);
        return vo;
    }
}
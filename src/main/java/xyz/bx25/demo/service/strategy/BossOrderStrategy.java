package xyz.bx25.demo.service.strategy;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import xyz.bx25.demo.common.enums.OrderStatusEnum;
import xyz.bx25.demo.common.enums.UserTypeEnum;
import xyz.bx25.demo.model.entity.WorkOrder;
import xyz.bx25.demo.model.vo.order.OrderListSimpleVO;
import xyz.bx25.demo.model.vo.order.list.OrderListSimpleBossVO;
import xyz.bx25.demo.service.AbstractOrderStrategy;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class BossOrderStrategy extends AbstractOrderStrategy {


    @Override
    public UserTypeEnum getSupportedRole() {
        return UserTypeEnum.BOSS;
    }

    @Override
    public LambdaQueryWrapper<WorkOrder> buildQueryWrapper(String userId, String tenantId, Integer status) {
        // 规则：查全租户 (直接返回基础 Wrapper 即可，不需要额外 ID 过滤)
        return createBaseWrapper(tenantId, status);
    }

    @Override
    public Page<? extends OrderListSimpleVO> convertPage(Page<WorkOrder> rawPage) {
        // 1. 准备数据
        List<WorkOrder> records = rawPage.getRecords();
        Map<String, String> deviceMap = getDeviceMap(records);

        // 2. 提取需要查询的用户ID (报修人 + 维修工)
        Set<String> userIds = new HashSet<>();
        records.forEach(o -> {
            if (StringUtils.hasText(o.getReporterId())) userIds.add(o.getReporterId());
            if (StringUtils.hasText(o.getRepairmanId())) userIds.add(o.getRepairmanId());
        });
        // 复用父类方法查人名
        Map<String, String> userMap = getUserMap(userIds);

        // 3. 转换
        return (Page<OrderListSimpleBossVO>) rawPage.convert(order -> {
            OrderListSimpleBossVO vo = new OrderListSimpleBossVO();
            BeanUtils.copyProperties(order, vo);

            vo.setStatusText(OrderStatusEnum.getDescByCode(order.getOrderStatus()));
            vo.setDeviceName(deviceMap.getOrDefault(order.getDeviceId(), "未知设备"));

            // 特有字段填充
            vo.setTotalAmount(order.getTotalAmount());
            vo.setReporterName(userMap.getOrDefault(order.getReporterId(), "未知"));
            vo.setRepairmanName(userMap.getOrDefault(order.getRepairmanId(), "待指派"));

            return vo;
        });
    }
    @Override
    public boolean hasPermission(WorkOrder order, String userId) {
        return true; // 只要是同租户的(Service层会查)，老板都能看
    }
}
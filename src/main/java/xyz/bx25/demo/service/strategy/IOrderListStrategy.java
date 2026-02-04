package xyz.bx25.demo.service.strategy;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import xyz.bx25.demo.common.enums.UserTypeEnum;
import xyz.bx25.demo.model.entity.WorkOrder;
import xyz.bx25.demo.model.vo.order.OrderListSimpleVO;

public interface IOrderListStrategy {

    /**
     * 策略匹配：当前策略支持哪种角色？
     */
    UserTypeEnum getSupportedRole();

    /**
     * 1. 构建查询条件 (Where语句)
     */
    LambdaQueryWrapper<WorkOrder> buildQueryWrapper(String userId, String tenantId, Integer status);

    /**
     * 2. 数据转换 (Entity Page -> VO Page)
     * 这里使用泛型 ? extends OrderSimpleVO，允许返回子类
     */
    Page<? extends OrderListSimpleVO> convertPage(Page<WorkOrder> rawPage);
}
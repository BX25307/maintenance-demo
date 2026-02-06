package xyz.bx25.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import xyz.bx25.demo.common.enums.UserTypeEnum;
import xyz.bx25.demo.model.entity.WorkOrder;
import xyz.bx25.demo.model.vo.order.OrderDetailVO;
import xyz.bx25.demo.model.vo.order.OrderListSimpleVO;

public interface IOrderStrategy {

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

    /**
     * 3. 【新增】详情页鉴权：当前用户是否有权查看该工单？
     * @param order 工单实体
     * @param userId 当前用户ID
     * @return true-允许查看, false-拒绝
     */
    boolean hasPermission(WorkOrder order, String userId);

    /**
     * 4. 【新增】详情页组装：根据角色过滤/填充字段
     * @param order 工单实体
     * @return 组装好的详情 VO
     */
    OrderDetailVO convertDetail(WorkOrder order);
}
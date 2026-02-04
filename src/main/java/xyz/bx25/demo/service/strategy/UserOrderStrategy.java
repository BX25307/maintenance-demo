import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.bx25.demo.common.enums.UserTypeEnum;
import xyz.bx25.demo.mapper.DeviceInfoMapper;
import xyz.bx25.demo.model.entity.WorkOrder;
import xyz.bx25.demo.model.vo.order.OrderListSimpleVO;
import xyz.bx25.demo.service.strategy.IOrderListStrategy;

@Component
public class UserOrderStrategy implements IOrderListStrategy {

    @Autowired
    private DeviceInfoMapper deviceMapper; // 只需要查设备名

    @Override
    public UserTypeEnum getSupportedRole() {
        return UserTypeEnum.USER;
    }

    @Override
    public LambdaQueryWrapper<WorkOrder> buildQueryWrapper(String userId, String tenantId, Integer status) {
        /* 规则：查自己 */
        return new LambdaQueryWrapper<WorkOrder>()
                .eq(WorkOrder::get, tenantId)
                .eq(WorkOrder::getReporterId, userId)
                .eq(status != null, WorkOrder::getOrderStatus, status)
                .orderByDesc(WorkOrder::getCreateTime);
    }

    @Override
    public Page<? extends OrderListSimpleVO> convertPage(Page<WorkOrder> rawPage) {
        // ... 此处省略 N+1 优化的 Map 查询代码 (查设备) ...

        return (Page<OrderListSimpleVO>) rawPage.convert(order -> {
            OrderListSimpleVO vo = new OrderListSimpleVO();
            BeanUtils.copyProperties(order, vo);
            //todo  vo.setDeviceName(...)
            return vo;
        });
    }
}
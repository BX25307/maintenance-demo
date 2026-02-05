package xyz.bx25.demo.model.vo.order.list;

import lombok.Data;
import lombok.EqualsAndHashCode;
import xyz.bx25.demo.model.vo.order.OrderListSimpleVO;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrderListSimpleRepairmanVO extends OrderListSimpleVO {
    // 维修工特有：必须知道去哪修、修什么
    private String addressDetail;
    private String faultDesc;
}

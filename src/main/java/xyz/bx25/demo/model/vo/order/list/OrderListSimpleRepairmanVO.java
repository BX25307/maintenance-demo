package xyz.bx25.demo.model.vo.order.list;

import xyz.bx25.demo.model.vo.order.OrderListSimpleVO;

public class OrderListSimpleRepairmanVO extends OrderListSimpleVO {
    // 维修工特有：必须知道去哪修、修什么
    private String addressDetail;
    private String faultDesc;
}

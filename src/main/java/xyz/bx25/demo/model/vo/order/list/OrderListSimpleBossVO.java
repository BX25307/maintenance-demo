package xyz.bx25.demo.model.vo.order.list;

import lombok.Data;
import lombok.EqualsAndHashCode;
import xyz.bx25.demo.model.vo.order.OrderListSimpleVO;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrderListSimpleBossVO extends OrderListSimpleVO {
    // 老板/管理员特有：关注人和钱
    private String reporterName;
    private String repairmanName;
    private BigDecimal totalAmount;
}

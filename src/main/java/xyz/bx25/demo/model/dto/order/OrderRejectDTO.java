package xyz.bx25.demo.model.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
public class OrderRejectDTO implements Serializable {
    @NotBlank(message = "工单ID不能为空")
    private String orderId;

    @NotBlank(message = "处理意见不能为空")
    private String auditRemark; // 如："经核实凭证无误，费用符合标准，请正常支付"
}

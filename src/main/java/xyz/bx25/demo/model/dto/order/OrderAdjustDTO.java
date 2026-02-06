package xyz.bx25.demo.model.dto.order;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderAdjustDTO implements Serializable {
    @NotBlank(message = "工单ID不能为空")
    private String orderId;

    @NotNull(message = "新材料费不能为空")
    @DecimalMin("0.00")
    private BigDecimal materialFee;

    @NotNull(message = "新人工费不能为空")
    @DecimalMin("0.00")
    private BigDecimal laborFee;

    @NotBlank(message = "处理备注不能为空")
    private String auditRemark; // 如："经核实，材料费偏高，已调整"
}

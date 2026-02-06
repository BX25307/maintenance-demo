package xyz.bx25.demo.model.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.io.Serializable;

@Data
public class OrderPayDTO implements Serializable {

    @NotBlank(message = "工单ID不能为空")
    private String orderId;

    // 后续可扩展支付密码
    // private String payPassword;
}
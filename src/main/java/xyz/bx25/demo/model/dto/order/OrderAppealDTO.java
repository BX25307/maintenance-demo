package xyz.bx25.demo.model.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class OrderAppealDTO implements Serializable {

    @NotBlank(message = "工单ID不能为空")
    private String orderId;

    @NotBlank(message = "申诉理由不能为空")
    private String appealReason;

//     可选：上传申诉凭证图片
     private List<String> appealImages;
}
package xyz.bx25.demo.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderSubmitDTO {
    /**
     * 关联设备ID (必填)
     */
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;

    /**
     * 故障描述 (必填)
     */
    @NotBlank(message = "故障描述不能为空")
    private String faultDesc;

    /**
     * 故障图片 (可选)
     */
    private String faultImages;
}

package xyz.bx25.demo.model.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 报修提交入参
 */
@Data
public class OrderSubmitDTO implements Serializable {

    @NotBlank(message = "设备ID不能为空")
    private String deviceId;

    @NotBlank(message = "故障描述不能为空")
    @Size(max = 500, message = "故障描述不能超过500字")
    private String faultDesc;

    /**
     * 关键字段：详细地址
     * 因为 device_info 表地址不准，所以报修时必须让用户填准确的
     */
    @NotBlank(message = "详细维修地址不能为空")
    @Size(max = 200, message = "地址长度不能超过200字")
    private String addressDetail;

    /**
     * 故障图片列表
     * 前端传 ["url1", "url2"]，后端转 JSON 存库
     */
    private List<String> faultImages;
}
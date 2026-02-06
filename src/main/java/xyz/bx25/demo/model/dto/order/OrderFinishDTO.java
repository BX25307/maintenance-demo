package xyz.bx25.demo.model.dto.order;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderFinishDTO implements Serializable {
    @NotBlank(message = "工单ID不能为空")
    private String orderId;

    /**
     * 维修结果描述 (做了什么操作)
     */
    @NotBlank(message = "维修结果描述不能为空")
    private String repairResult;

    /**
     * 完工凭证图片 (必填，证明修好了)
     */
    @NotNull(message = "请上传完工凭证")
    private List<String> repairImages;

    /**
     * 材料费 (实报实销，不抽成)
     * 必须 >= 0
     */
    @NotNull(message = "材料费不能为空")
    @DecimalMin(value = "0.00", message = "材料费不能小于0")
    private BigDecimal materialFee;


    private BigDecimal laborFee;

    /**
     * 完工凭证图片 (必填，证明修好了)
     */
    @NotNull(message = "请上传材料费凭证")
    private List<String> feeImages;


}

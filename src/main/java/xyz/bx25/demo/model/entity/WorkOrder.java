package xyz.bx25.demo.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Builder;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 维修工单表
 * 包含：业务信息、详细地址、财务结算、申诉记录
 */
@Builder
@Data
@TableName("work_order")
public class WorkOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.INPUT) // 或 IdType.INPUT，取决于你是否自己生成ID
    private String orderId;

    /**
     * 业务展示单号 (如 WO20231027001)
     */
    private String orderSn;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 关联设备ID
     */
    private String deviceId;
    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 报修人ID
     */
    private String reporterId;

    /**
     * 设备老板ID
     */
    private String ownerId;

    /**
     * 维修工ID (待接单时为空)
     */
    private String repairmanId;

    /**
     * 详细维修地址 (报修时手动填入，快照保存)
     */
    private String addressDetail;

    /**
     * 故障描述
     */
    private String faultDesc;

    /**
     * 故障图片 (JSON数组)
     */
    private String faultImages;

    /**
     * 维修结果/完工备注
     */
    private String repairResult;

    /**
     * 完工凭证图片 (JSON数组)
     */
    private String repairImages;

    /**
     * 订单状态
     *
     * @see xyz.bx25.demo.common.enums.OrderStatusEnum
     * 0:待接单 1:维修中 3:待支付 4:已取消 5:已完成 6:申诉中
     */
    private Integer orderStatus;

    // ================= 财务结算字段 =================

    /**
     * 材料费 (实报实销)
     */
    private BigDecimal materialFee;

    /**
     * 人工费 (技术服务费，需抽成)
     */
    private BigDecimal laborFee;

    /**
     * 总金额 (老板应付 = 材料+人工)
     */
    private BigDecimal totalAmount;

    /**
     * 费率快照 (如 0.10)
     */
    private BigDecimal platformRate;

    /**
     * 平台分润 (人工 * 费率)
     */
    private BigDecimal platformIncome;

    /**
     * 维修工实收 (总额 - 平台分润)
     */
    private BigDecimal repairmanIncome;

    // ================= 申诉字段 =================

    /**
     * 老板申诉理由
     */
    private String appealReason;
}
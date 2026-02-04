package xyz.bx25.demo.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 资金流水表
 * 记录：老板扣款、维修工入账、平台抽成
 */
@Data
@TableName("capital_flow")
public class CapitalFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 流水ID (自增) */
    @TableId(type = IdType.AUTO)
    private Long flowId;

    /** 交易流水号 (唯一) */
    private String tradeNo;

    /** 关联工单ID */
    private String orderId;

    /** * 资金归属账户ID
     * (SysUser.userId 或 平台特殊账号 'PLATFORM_SYS')
     */
    private String accountId;

    /** * 交易类型
     * @see xyz.bx25.demo.common.enums.FlowTypeEnum
     * PAY(支出), INCOME(收入), REFUND(退款)
     */
    private String flowType;

    /** 变动金额 (支出为负，收入为正) */
    private BigDecimal amount;

    /** 余额快照 (变动后的余额) */
    private BigDecimal balanceSnapshot;

    /** 备注 */
    private String remark;

    /** 租户ID */
    private String tenantId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
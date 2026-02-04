package xyz.bx25.demo.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Builder;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 工单操作日志表
 */
@Builder
@Data
@TableName("work_order_log")
public class WorkOrderLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long logId;

    /** 关联工单ID */
    private String orderId;

    /** 操作人ID */
    private String operatorId;

    /**
     * 操作人角色
     * 标识执行操作的用户类型
     * @see xyz.bx25.demo.common.enums.UserTypeEnum
     * 可选值：USER(普通用户), BOSS(老板), REPAIRMAN(维修员), ADMIN(管理员)
     */
    private String operatorRole;

    /**
     * 动作类型
     * 标识对工单执行的具体操作
     * @see xyz.bx25.demo.common.enums.ActionTypeEnum
     * 可选值：CREATE(创建工单), ASSIGN(指派维修员), TRANSFER(转单), 
     *        FINISH(完成维修), APPEAL(申诉), PAY(支付), CANCEL(取消工单)
     */
    private String actionType;

    /** 操作备注/详情 */
    private String actionDesc;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
package xyz.bx25.demo.model.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 工单操作流水日志表
 * </p>
 *
 * @author Bx25
 */
@Data
@TableName("work_order_log")
public class WorkOrderLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志ID (数据库自增)
     */
    @TableId(type = IdType.AUTO)
    private Long logId;

    /**
     * 关联的工单ID
     */
    private String orderId;

    /**
     * 操作人ID
     */
    private String operatorId;

    /**
     * 操作人角色
     * USER, ADMIN, REPAIRMAN
     */
    private String operatorRole;

    /**
     * 动作类型
     * CREATE, ASSIGN, START, FINISH, CANCEL
     * (建议对应 enums 包下的 ActionTypeEnum)
     */
    private String actionType;

    /**
     * 动作备注/描述
     */
    private String actionDesc;

    /**
     * 操作时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
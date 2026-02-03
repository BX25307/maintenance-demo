package xyz.bx25.demo.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 维修工单主表
 * </p>
 *
 * @author Bx25
 */
@Builder
@Data
@TableName("work_order")
public class WorkOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工单唯一标识 (主键)
     * type = IdType.ASSIGN_ID: 自动生成雪花算法ID (如果是String类型会转为字符串)
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String orderId;

    /**
     * 关联的设备ID
     */
    private String deviceId;

    /**
     * 老板ID (冗余存储，优化查询)
     */
    private String ownerId;

    /**
     * 报修人ID (扫码的用户)
     */
    private String reporterId;

    /**
     * 派单管理员ID
     */
    private String adminId;

    /**
     * 维修员ID
     */
    private String repairmanId;

    /**
     * 故障描述
     */
    private String faultDesc;

    /**
     * 故障图片 (JSON或URL字符串)
     */
    private String faultImages;

    /**
     * 维修结果反馈
     */
    private String repairResult;

    /**
     * 工单状态
     * 0:待派单, 1:已派单, 2:维修中, 3:已完成, 4:已取消
     * (建议对应 enums 包下的 OrderStatusEnum)
     */
    private Integer orderStatus;

    /**
     * 租户ID (多租户隔离)
     */
    private String tenantId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT) // 自动填充
    private LocalDateTime createTime;

    /**
     * 派单时间
     */
    private LocalDateTime assignTime;

    /**
     * 完结时间
     */
    private LocalDateTime finishTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE) // 自动填充
    private LocalDateTime updateTime;

    /**
     * 逻辑删除 (0:未删除, 1:已删除)
     */
    @TableLogic
    private Integer isDeleted;
}
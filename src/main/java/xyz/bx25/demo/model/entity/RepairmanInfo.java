package xyz.bx25.demo.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 维修人员业务扩展表 (1:1 关联 SysUser)
 * </p>
 *
 * @author Bx25
 */
@Data
@TableName("repairman_info")
public class RepairmanInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 维修工信息唯一标识 (主键)
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String repairmanId;

    /**
     * 关联的用户ID (核心外键 -> sys_user.user_id)
     * 通过这个字段去 sys_user 表查名字和电话
     */
    private String userId;

    /**
     * 业务状态
     * 0:空闲-可接单, 1:忙碌-维修中
     * (建议对应 model.enums.WorkStatusEnum)
     */
    private Integer workStatus;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除
     * 0:正常, 1:已删除
     */
    @TableLogic
    private Integer isDeleted;
}
package xyz.bx25.demo.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 系统统一用户基础信息表
 * </p>
 *
 * @author Bx25
 */
@Data
@TableName("sys_user")
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户唯一标识 (主键)
     * 使用 ASSIGN_ID (雪花算法) 生成唯一字符串ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String userId;

    /**
     * 登录账号 (唯一索引)
     */
    private String username;

    /**
     * 登录密码 (加密存储)
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 联系手机号
     */
    private String phone;

    /**
     * 用户头像地址
     */
    private String avatar;

    /**
     * 角色标识
     * ADMIN-管理员, BOSS-老板/普通用户, REPAIRMAN-维修员
     * (后续建议配合 Spring Security 权限注解使用)
     */
    private String roleKey;

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
package xyz.bx25.demo.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 系统用户表
 */
@Data
@TableName("sys_user")
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private String userId;

    private String username;

    private String password;

    private String realName;

    private String phone;

    private String avatar;

    /** * 角色标识
     * BOSS, REPAIRMAN, ADMIN, USER
     */
    private String roleKey;

    /** 💰 钱包余额 (老板充值扣费/维修工提现) */
    private BigDecimal balance;

    private String tenantId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
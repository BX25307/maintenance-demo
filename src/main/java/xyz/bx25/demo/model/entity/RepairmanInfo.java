package xyz.bx25.demo.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 维修工扩展信息表
 */
@Data
@TableName("repairman_info")
public class RepairmanInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 维修工ID (通常与 userId 一致，或独立生成) */
    @TableId(type = IdType.INPUT)
    private String repairmanId;

    /** 关联系统用户ID */
    private String userId;

    /** * 工作状态
     * 0:空闲 1:忙碌
     */
    private Integer workStatus;

    /** 服务省份 (如 "广东省") */
    private String provinceName;

    /** 服务城市 (如 "深圳市") */
    private String cityName;

    private String tenantId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
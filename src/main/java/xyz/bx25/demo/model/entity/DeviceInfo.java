package xyz.bx25.demo.model.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 设备信息表
 * </p>
 *
 * @author Bx25
 */
@Data
@TableName("device_info")
public class DeviceInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备ID (主键)
     */
    @TableId
    private String deviceId;


    /**
     * 设备归属人ID (老板)
     */
    private String userId;

    /**
     * SN序列号
     */
    private String sn;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备状态
     * DELIVERING, HEALTHY, REMOVED, ERROR
     */
    private String deviceStatus;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 平台类型
     */
    private String platformType;

    // --- 地理位置与网络信息 ---

    private String provinceName;
    private String provinceCode;
    private String cityName;
    private String cityCode;
    private String countyName;
    private String countyCode;
    private String deviceRegionId;

    /**
     * 运营商 (TELECOM, UNICOM, MOBILE)
     */
    private String isp;

    private Integer lineCount;
    private Integer userSingleUplinkMbps;
    private Integer upSingleUplinkMbps;
    private Integer userTotalBandwidthMbps;
    private Integer upTotalBandwidthMbps;

    // --- 其他业务字段 ---

    private LocalDateTime allocationTime;
    private LocalDateTime expireTime;
    private LocalDateTime bindingTime;
    private LocalDate incomeDate; // 注意这里是 Date 类型

    private String deviceRemark;
    private String deviceRuleId;
    private String deviceInfoId;
    private String businessInfoId;
    private String projectId;
    private String orderId; // 这里的order_id可能是设备采购单，不是维修工单，需注意区分

    // --- 系统字段 ---

    private String tenantId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
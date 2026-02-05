package xyz.bx25.demo.model.vo;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailVO implements Serializable {
    // --- 基础信息 ---
    private String orderId;
    private String orderSn;
    private String deviceId;
    private String deviceName; // 需关联查询
    private String deviceSn;   // 辅助设备识别

    // --- 核心业务 ---
    private String faultDesc;
    private String faultImages; // JSON string or List
    private String addressDetail; // 📍 核心：维修地址

    // --- 状态与人员 ---
    private Integer orderStatus;
    private String statusText;
    private String reporterName;
    private String repairmanName;

    // --- 结果与财务 (根据权限动态填充) ---
    private String repairResult;
    private String repairImages;

    private BigDecimal materialFee;
    private BigDecimal laborFee;
    private BigDecimal totalAmount; // 用户/老板可见

    // --- 时间轴 ---
    private LocalDateTime createTime;
    private LocalDateTime dispatchTime;
    private LocalDateTime finishTime;
    private LocalDateTime payTime;

}
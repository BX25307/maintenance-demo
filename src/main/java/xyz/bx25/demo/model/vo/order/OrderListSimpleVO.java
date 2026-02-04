package xyz.bx25.demo.model.vo.order;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 工单列表项 VO (轻量级)
 */
@Data
public class OrderListSimpleVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String orderId;

    /** 业务单号 (WO2023...) */
    private String orderSn;

    /** 设备名称 (需要查询 device_info 拼接) */
    private String deviceName;

    private String deviceId;
    /** 状态码 */
    private Integer orderStatus;

    /** 状态文本 (待接单、维修中...) */
    private String statusText;

    /** 报修时间 */
    private LocalDateTime createTime;

}
package xyz.bx25.demo.model.vo;

import lombok.Builder;
import lombok.Data;
import xyz.bx25.demo.common.enums.OrderStatusEnum;

import java.time.LocalDateTime;
@Builder
@Data
public class OrderDetailVO {
    private String orderId;// 工单号
    private String deviceId;   // 设备ID

    private String faultDesc;  // 故障描述
    private String faultImages;

    private String reporterId; // 报修人
    private LocalDateTime createTime;

    private Integer orderStatus; // 状态码 (1,2,3)
    private String statusText;   // 状态文本 (待派单, 维修中...)

    private String repairmanId;  // 维修工
    private LocalDateTime assignTime;

    private String repairResult;
    private LocalDateTime finishTime;


}

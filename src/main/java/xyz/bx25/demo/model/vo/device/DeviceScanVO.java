package xyz.bx25.demo.model.vo.device;

import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class DeviceScanVO {
    private String deviceId;
    private String deviceName;
    private String sn;

}
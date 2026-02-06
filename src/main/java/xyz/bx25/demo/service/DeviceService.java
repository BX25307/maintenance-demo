package xyz.bx25.demo.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.bx25.demo.common.util.UserContext;
import xyz.bx25.demo.mapper.DeviceInfoMapper;
import xyz.bx25.demo.model.entity.DeviceInfo;
import xyz.bx25.demo.model.vo.device.DeviceScanVO;

@Service
public class DeviceService extends ServiceImpl<DeviceInfoMapper, DeviceInfo> {

    public DeviceScanVO getDeviceById(String deviceId) {
        String tenantId = UserContext.getTenantId();
        DeviceInfo deviceInfo = this.lambdaQuery()
                .eq(DeviceInfo::getDeviceId, deviceId)
                .eq(DeviceInfo::getTenantId, tenantId)
                .one();
        if(deviceInfo==null){
            return null;
        }
        DeviceScanVO deviceScanVO = DeviceScanVO.builder()
                .deviceId(deviceInfo.getDeviceId())
                .deviceName(deviceInfo.getDeviceName())
                .sn(deviceInfo.getSn())
                .build();
        return deviceScanVO;
    }
}

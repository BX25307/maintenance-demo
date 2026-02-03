package xyz.bx25.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.bx25.demo.common.Response;
import xyz.bx25.demo.common.util.UserContext;
import xyz.bx25.demo.mapper.DeviceInfoMapper;
import xyz.bx25.demo.model.entity.DeviceInfo;
import xyz.bx25.demo.model.vo.DeviceScanVO;
import xyz.bx25.demo.service.DeviceService;

@RestController
@RequestMapping("/api/device")
public class DeviceController {
    @Autowired
    private DeviceService deviceService;
    /**
     * 扫码后获取设备详情 (用于回显设备名称、型号等)
     * GET /api/device/scan/{deviceId}
     */
    @GetMapping("/scan/{deviceId}")
    public Response<DeviceScanVO> scanDevice(@PathVariable String deviceId) {
         DeviceScanVO deviceScanVO=deviceService.getDeviceById(deviceId);
         if(deviceScanVO==null){
             return Response.error("设备不存在或无权访问");
         }
        return Response.success(deviceScanVO);
    }
}
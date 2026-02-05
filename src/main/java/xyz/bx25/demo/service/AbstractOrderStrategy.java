package xyz.bx25.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import xyz.bx25.demo.common.enums.OrderStatusEnum;
import xyz.bx25.demo.mapper.DeviceInfoMapper;
import xyz.bx25.demo.mapper.SysUserMapper;
import xyz.bx25.demo.model.entity.DeviceInfo;
import xyz.bx25.demo.model.entity.SysUser;
import xyz.bx25.demo.model.entity.WorkOrder;
import xyz.bx25.demo.model.entity.WorkOrderLog;
import xyz.bx25.demo.model.vo.OrderDetailVO;
import xyz.bx25.demo.service.IOrderStrategy;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 策略基类：封装公用的 Mapper 和查询逻辑
 */
public abstract class AbstractOrderStrategy implements IOrderStrategy {

    @Autowired
    protected DeviceInfoMapper deviceInfoMapper; // 子类可见
    @Autowired
    protected SysUserMapper sysUserMapper;       // 子类可见

    /**
     * 1. 构建基础查询条件 (所有策略都通用)
     * 包含：租户隔离、按时间倒序、状态筛选
     */
    protected LambdaQueryWrapper<WorkOrder> createBaseWrapper(String tenantId, Integer status) {
        return new LambdaQueryWrapper<WorkOrder>()
                .eq(WorkOrder::getTenantId, tenantId)
                .eq(status != null, WorkOrder::getOrderStatus, status)
                .orderByDesc(WorkOrder::getCreateTime);
    }

    /**
     * 2. 公用方法：批量查询设备名称 Map
     */
    protected Map<String, String> getDeviceMap(List<WorkOrder> orders) {
        if (orders.isEmpty()) return Collections.emptyMap();

        Set<String> deviceIds = orders.stream()
                .map(WorkOrder::getDeviceId)
                .collect(Collectors.toSet());

        if (deviceIds.isEmpty()) return Collections.emptyMap();

        List<DeviceInfo> list = deviceInfoMapper.selectBatchIds(deviceIds);
        return list.stream().collect(Collectors.toMap(DeviceInfo::getDeviceId, DeviceInfo::getDeviceName));
    }

    /**
     * 3. 公用方法：批量查询用户姓名 Map (ID -> RealName)
     */
    protected Map<String, String> getUserMap(Set<String> userIds) {
        if (userIds.isEmpty()) return Collections.emptyMap();

        List<SysUser> list = sysUserMapper.selectBatchIds(userIds);
        return list.stream().collect(Collectors.toMap(
                SysUser::getUserId,
                u -> StringUtils.hasText(u.getRealName()) ? u.getRealName() : u.getUsername()
        ));
    }

    /**
     * 默认鉴权逻辑 (子类可覆盖)
     */
    @Override
    public boolean hasPermission(WorkOrder order, String userId) {
        return true; // 默认放行 (Boss策略用)
    }

    /**
     * 通用的详情转换逻辑
     */
    @Override
    public OrderDetailVO convertDetail(WorkOrder order) {
        OrderDetailVO vo = new OrderDetailVO();
        BeanUtils.copyProperties(order, vo); // 复制同名基础字段

        // 1. 查设备名
        DeviceInfo device = deviceInfoMapper.selectById(order.getDeviceId());
        if (device != null) {
            vo.setDeviceName(device.getDeviceName());
            vo.setDeviceSn(device.getSn());
        }

        // 2. 查人名
        if (StringUtils.hasText(order.getReporterId())) {
            SysUser u = sysUserMapper.selectById(order.getReporterId());
            vo.setReporterName(u != null ? u.getRealName() : "未知用户");
        }
        if (StringUtils.hasText(order.getRepairmanId())) {
            SysUser u = sysUserMapper.selectById(order.getRepairmanId());
            vo.setRepairmanName(u != null ? u.getRealName() : "待指派");
        }

        // 4. 状态文本
        vo.setStatusText(OrderStatusEnum.getDescByCode(order.getOrderStatus()));

        return vo;
    }
}
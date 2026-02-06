package xyz.bx25.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import xyz.bx25.demo.common.constants.RedisKeyConstants;
import xyz.bx25.demo.common.enums.ActionTypeEnum;
import xyz.bx25.demo.common.enums.OrderStatusEnum;
import xyz.bx25.demo.common.enums.UserTypeEnum;
import xyz.bx25.demo.common.enums.WorkStatusEnum;
import xyz.bx25.demo.common.exception.BusinessException;
import xyz.bx25.demo.common.util.UserContext;
import xyz.bx25.demo.mapper.RepairmanInfoMapper;
import xyz.bx25.demo.mapper.SysUserMapper;
import xyz.bx25.demo.model.entity.RepairmanInfo;
import xyz.bx25.demo.model.entity.SysUser;
import xyz.bx25.demo.model.entity.WorkOrder;
import xyz.bx25.demo.model.vo.RepairmanVO;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RepairmanService extends ServiceImpl<RepairmanInfoMapper, RepairmanInfo> {
    @Autowired
    private SysUserMapper sysUserMapper;

    public List<RepairmanVO> listFreeRepairmen(String cityName) {
        String tenantId = UserContext.getTenantId();

        // 1. 查询符合条件的维修工基础信息 (状态为空闲 + 租户隔离)
        LambdaQueryWrapper<RepairmanInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RepairmanInfo::getTenantId, tenantId)
                .eq(RepairmanInfo::getWorkStatus, WorkStatusEnum.FREE.getCode()); // 只查空闲的

        // 如果传了城市，进行筛选
        if (StringUtils.hasText(cityName)) {
            wrapper.eq(RepairmanInfo::getCityName, cityName);
        }

        List<RepairmanInfo> infoList = baseMapper.selectList(wrapper);

        if (CollectionUtils.isEmpty(infoList)) {
            return Collections.emptyList();
        }

        // 2. 提取 UserId 列表，批量查询姓名和电话
        Set<String> userIds = infoList.stream()
                .map(RepairmanInfo::getUserId)
                .collect(Collectors.toSet());

        List<SysUser> userList = sysUserMapper.selectBatchIds(userIds);
        Map<String, SysUser> userMap = userList.stream()
                .collect(Collectors.toMap(SysUser::getUserId, u -> u));

        // 3. 组装 VO
        return infoList.stream().map(info -> {
            SysUser user = userMap.get(info.getUserId());
            return RepairmanVO.builder()
                    .repairmanId(info.getRepairmanId()) // 派单时用这个ID
                    .userId(info.getUserId())
                    .realName(user != null ? user.getRealName() : "未知")
                    .phone(user != null ? user.getPhone() : "")
                    .cityName(info.getCityName())
                    .workStatus(info.getWorkStatus())
                    .build();
        }).collect(Collectors.toList());
    }
}

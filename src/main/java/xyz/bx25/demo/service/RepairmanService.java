package xyz.bx25.demo.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.bx25.demo.common.constants.RedisKeyConstants;
import xyz.bx25.demo.common.enums.ActionTypeEnum;
import xyz.bx25.demo.common.enums.OrderStatusEnum;
import xyz.bx25.demo.common.enums.UserTypeEnum;
import xyz.bx25.demo.common.enums.WorkStatusEnum;
import xyz.bx25.demo.common.exception.BusinessException;
import xyz.bx25.demo.common.util.UserContext;
import xyz.bx25.demo.mapper.RepairmanInfoMapper;
import xyz.bx25.demo.model.entity.RepairmanInfo;
import xyz.bx25.demo.model.entity.WorkOrder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class RepairmanService extends ServiceImpl<RepairmanInfoMapper, RepairmanInfo> {

}

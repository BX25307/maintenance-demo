package xyz.bx25.demo.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.bx25.demo.mapper.RepairmanInfoMapper;
import xyz.bx25.demo.model.entity.RepairmanInfo;

import java.util.List;
import java.util.Map;
@Service
public class RepairmanService extends ServiceImpl<RepairmanInfoMapper, RepairmanInfo> {

    public List<Map<String, Object>> getIdleRepairmen() {
    }
}

package xyz.bx25.demo.model.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RepairmanVO {
    private String repairmanId; // 核心：派单时传这个ID
    private String userId;
    private String realName;    // 显示用
    private String phone;       // 联系用
    private String cityName;    // 所在城市
    private Integer workStatus; // 0:空闲 1:忙碌
}
package xyz.bx25.demo.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum OrderStatusEnum {
    PENDING(0, "待派单"),
    DISPATCHED(1, "已派单"),
    REPAIRING(2, "维修中"),
    FINISHED(3, "已完成"),
    CANCELLED(4, "已取消");

    @EnumValue
    private final int code;
    private final String desc;

    OrderStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
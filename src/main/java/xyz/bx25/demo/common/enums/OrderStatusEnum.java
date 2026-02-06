package xyz.bx25.demo.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum OrderStatusEnum {
    PENDING(0, "待派单"),
    DISPATCHED(1, "已派单"),
    PAYING(2, "待支付"),
    FINISHED(3, "已完成"),
    CANCELLED(4, "已取消"),
    APPEAL(5, "申诉中");

    @EnumValue
    private final int code;
    private final String desc;

    OrderStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer orderStatus) {
        for (OrderStatusEnum value : values()) {
            if (value.code == orderStatus) {
                return value.desc;
            }
        }
        return null;
    }
}
package xyz.bx25.demo.common.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum WorkStatusEnum {
    FREE(0, "空闲"),
    BUSY(1, "忙碌");

    @EnumValue // 标记存入数据库的值
    private final int code;
    private final String desc;

    WorkStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
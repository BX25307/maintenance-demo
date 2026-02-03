package xyz.bx25.demo.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum UserTypeEnum {

    USER("USER", "用户"),
    REPAIRMAN("REPAIRMAN", "维修员"),
    ADMIN("ADMIN", "管理员"),
    BOSS("BOSS", "老板");

    @EnumValue
    private final String code;
    private final String desc;

    UserTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public static String getDescByCode(String code) {
        for (UserTypeEnum value : UserTypeEnum.values()) {
            if (value.getCode().equals(code)) {
                return value.getDesc();
            }
        }
        return null;
    }
}

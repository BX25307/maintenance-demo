package xyz.bx25.demo.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum ActionTypeEnum {
    CREATE("CREATE", "创建工单"),
    ASSIGN("ASSIGN", "管理员派单"),
    START("START", "开始维修"),
    FINISH("FINISH", "完结工单"),
    CANCEL("CANCEL", "取消工单");

    @EnumValue
    private final String code;
    private final String desc;

    ActionTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
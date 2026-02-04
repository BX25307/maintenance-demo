package xyz.bx25.demo.common.enums;

import lombok.Getter;

/**
 * 资金流水类型枚举
 * 用于标识资金流水的交易类型
 */
@Getter
public enum FlowTypeEnum {

    /**
     * 支出 - 资金流出
     */
    PAY("PAY", "支出"),

    /**
     * 收入 - 资金流入
     */
    INCOME("INCOME", "收入"),

    /**
     * 退款 - 资金退回
     */
    REFUND("REFUND", "退款");

    private final String code;
    private final String description;

    FlowTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取对应的枚举值
     *
     * @param code 代码值
     * @return 对应的枚举值，如果找不到则返回null
     */
    public static FlowTypeEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (FlowTypeEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 判断给定的code是否有效
     *
     * @param code 代码值
     * @return 是否有效
     */
    public static boolean isValid(String code) {
        return getByCode(code) != null;
    }
}
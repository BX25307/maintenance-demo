package xyz.bx25.demo.common.enums;

/**
 * 工单操作类型枚举
 * 用于标识工单日志中的操作动作类型
 */
public enum ActionTypeEnum {

    /**
     * 创建工单 - 用户提交报修申请
     */
    CREATE("CREATE", "创建工单"),

    /**
     * 指派维修员 - 管理员分配维修任务
     */
    ASSIGN("ASSIGN", "指派维修员"),

    /**
     * 转单 - 将工单转移给其他维修员
     */
    TRANSFER("TRANSFER", "转单"),

    /**
     * 完成维修 - 维修员标记维修完成
     */
    FINISH("FINISH", "完成维修"),

    /**
     * 申诉 - 对维修结果提出申诉
     */
    APPEAL("APPEAL", "申诉"),

    /**
     * 支付 - 完成维修费用支付
     */
    PAY("PAY", "支付"),

    /**
     * 取消 - 用户取消工单
     */
    CANCEL("CANCEL", "取消");



    private final String code;
    private final String description;

    ActionTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据code获取对应的枚举值
     *
     * @param code 代码值
     * @return 对应的枚举值，如果找不到则返回null
     */
    public static ActionTypeEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (ActionTypeEnum value : values()) {
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

    /**
     * 获取操作类型的中文描述
     *
     * @param code 操作类型代码
     * @return 中文描述，如果找不到则返回code本身
     */
    public static String getDescriptionByCode(String code) {
        ActionTypeEnum enumValue = getByCode(code);
        return enumValue != null ? enumValue.getDescription() : code;
    }
}
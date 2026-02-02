package xyz.bx25.demo.common.util;

public class UserContext {
    private static final ThreadLocal<String> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> TENANT_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> ROLE_KEY = new ThreadLocal<>();

    public static void set(String userId, String tenantId, String roleKey) {
        USER_ID.set(userId);
        TENANT_ID.set(tenantId);
        ROLE_KEY.set(roleKey);
    }

    public static String getUserId() {
        return USER_ID.get();
    }

    public static String getTenantId() {
        return TENANT_ID.get();
    }

    public static String getRoleKey() {
        return ROLE_KEY.get();
    }

    public static void clear() {
        USER_ID.remove();
        TENANT_ID.remove();
        ROLE_KEY.remove();
    }
}
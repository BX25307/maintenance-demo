package xyz.bx25.demo.common.constants;

public class RedisKeyConstants {
    private static final String BASE_PREFIX = "demo:";
    private static final String lOGIN_TOKEN_PREFIX = BASE_PREFIX + "auth:token:";
    private static final String LOCK_ORDER_PREFIX = BASE_PREFIX + "lock:order";

    public static String getLoginTokenKey(String userId){
        return lOGIN_TOKEN_PREFIX + userId;
    }

    public static String getLockOrderKey(String orderId){
        return LOCK_ORDER_PREFIX;
    }
}

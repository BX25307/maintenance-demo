package xyz.bx25.demo.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "maintenance-secret-key-bx25";
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000L;//24小时

    /**
     * 生成 Token (增加 tenantId 参数)
     */
    public String createToken(String userId, String roleKey, String tenantId) {
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

        return JWT.create()
                .withSubject(userId)
                .withClaim("roleKey", roleKey)
                .withClaim("tenantId", tenantId) // 核心：注入租户ID
                .withExpiresAt(date)
                .withIssuedAt(new Date())
                .sign(algorithm);
    }

    /**
     * 校验并返回 DecodedJWT
     */
    public DecodedJWT verifyToken(String token) {
        if(!StringUtils.hasText(token)){
            return null;
        }
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            JWTVerifier verifier = JWT.require(algorithm).build();
            return verifier.verify(token);
        } catch (JWTVerificationException exception) {
            return null;
        }
    }

    // --- 便捷获取方法 ---

    public String getUserId(String token) {
        DecodedJWT jwt = verifyToken(token);
        return jwt != null ? jwt.getSubject() : null;
    }

    public String getTenantId(String token) {
        DecodedJWT jwt = verifyToken(token);
        // 如果 token 无效或没有 tenantId，这里可能需要处理空指针，或者在调用层处理
        return jwt != null ? jwt.getClaim("tenantId").asString() : null;
    }

    public String getRole(String token) {
        DecodedJWT jwt = verifyToken(token);
        return jwt != null ? jwt.getClaim("role").asString() : null;
    }
}
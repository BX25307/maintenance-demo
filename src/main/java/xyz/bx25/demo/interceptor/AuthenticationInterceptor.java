package xyz.bx25.demo.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import xyz.bx25.demo.common.Response;
import xyz.bx25.demo.common.util.JwtUtil;
import xyz.bx25.demo.common.util.UserContext;

import java.io.IOException;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 获取 Token (通常在 Header 的 Authorization 字段，格式 "Bearer xxx")
        String authHeader = request.getHeader("Authorization");

        // 简单处理：如果没有 Header 或者格式不对，直接拒绝
        if (!StringUtils.hasText(authHeader)) {
            return handleAuthFail(response, "未携带token或token无效");
        }

        // 去掉 "Bearer " 前缀（如果前端传了的话），这里做个简单兼容
        String token = authHeader.replace("Bearer ", "").trim();

        // 2. 解析 Token 关键信息
        String userId = jwtUtil.getUserId(token);
        String tenantId = jwtUtil.getTenantId(token);
        String roleKey = jwtUtil.getRole(token);

        // 3. 【核心防御】校验是否存在
        // 如果 Token 被篡改，或者字段缺失，这里会是 null
        if (!StringUtils.hasText(userId) || !StringUtils.hasText(tenantId)|| !StringUtils.hasText(roleKey)) {
            return handleAuthFail(response, "token无效");
        }

        // 4. 存入 ThreadLocal，后续 Service 直接用
        UserContext.set(userId, tenantId, roleKey);

        return true; // 放行
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求结束，必须清理 ThreadLocal，防止内存泄漏或线程复用导致的数据混乱
        UserContext.clear();
    }

    private boolean handleAuthFail(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(401); // 设置 HTTP 状态码
        response.setContentType("application/json;charset=UTF-8"); // 设置响应类型

        // 构建统一响应对象 (假设你的 Response 类有 error 方法)
        Response<Void> errorResponse = Response.error(msg);

        // 序列化成 JSON 字符串 (这里使用 FastJson，也可以用 Jackson)
        String jsonString = com.alibaba.fastjson.JSON.toJSONString(errorResponse);

        // 写入响应流
        response.getWriter().write(jsonString);

        return false; // 拦截请求
    }
}
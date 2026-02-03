package xyz.bx25.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.bx25.demo.common.Response;
import xyz.bx25.demo.model.dto.LoginDTO;
import xyz.bx25.demo.model.vo.LoginVO;
import xyz.bx25.demo.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户登录
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public Response<LoginVO> login(@RequestBody @Validated LoginDTO loginDTO) {
        LoginVO vo = authService.login(loginDTO);
        return Response.success(vo);
    }

    /**
     * 退出登录 (可选)
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public Response<Void> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return Response.success();
    }
}
package xyz.bx25.demo.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank; // 记得引入 validation 依赖

@Data
public class LoginDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
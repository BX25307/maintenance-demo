package xyz.bx25.demo.model.vo;

import lombok.Data;

@Data
public class LoginVO {
    private String token;      // 核心：访问令牌
    private String userId;     // 用户ID
    private String realName;   // 姓名 (显示用)
    private String roleKey;    // 角色 (用于前端控制菜单权限)
    private String avatar;     // 头像
}
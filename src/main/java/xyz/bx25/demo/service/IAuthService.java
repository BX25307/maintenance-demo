package xyz.bx25.demo.service;

import xyz.bx25.demo.model.dto.LoginDTO;
import xyz.bx25.demo.model.vo.LoginVO;

public interface IAuthService {
    LoginVO login(LoginDTO loginDTO);

    void logout(String token);
}

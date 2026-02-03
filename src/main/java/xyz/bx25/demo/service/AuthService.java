package xyz.bx25.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jodd.util.StringUtil;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.bx25.demo.common.constants.RedisKeyConstants;
import xyz.bx25.demo.common.util.JwtUtil;
import xyz.bx25.demo.mapper.SysUserMapper;
import xyz.bx25.demo.model.dto.LoginDTO;
import xyz.bx25.demo.model.entity.SysUser;
import xyz.bx25.demo.model.vo.LoginVO;

import java.time.Duration;

@Service
public class AuthService extends ServiceImpl<SysUserMapper, SysUser> {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RedissonClient redissonClient;
    public LoginVO login(LoginDTO loginDTO) {
        //查询
        SysUser user = this.lambdaQuery()
                .eq(SysUser::getUsername, loginDTO.getUsername())
                .one();
        //验证
        if(user==null||!user.getPassword().equals(loginDTO.getPassword())){
            throw new RuntimeException("用户名或密码错误");
        }
        //生成token
        String token = jwtUtil.createToken(user.getUserId(), user.getRoleKey(), user.getTenantId());
        //存入redis
        String redisKey = RedisKeyConstants.getLoginTokenKey(user.getUserId());
        RBucket<String> bucket = redissonClient.getBucket(redisKey);
        bucket.set(token, Duration.ofHours(24));
        //组装VO
        LoginVO loginVO = LoginVO.builder()
                .token(token)
                .roleKey(user.getRoleKey())
                .avatar(user.getAvatar())
                .realName(user.getRealName())
                .userId(user.getUserId())
                .build();
        return loginVO;

    }

    public void logout(String token) {
        String userId = jwtUtil.getUserId(token);
        if(StringUtils.hasText(userId)){
            String redisKey = RedisKeyConstants.getLoginTokenKey(userId);
            redissonClient.getBucket(redisKey).delete();
        }
    }
}

package com.lcyy.aicloud.service.impl;

import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lcyy.aicloud.model.dto.UserDTO;
import com.lcyy.aicloud.model.entity.User;
import com.lcyy.aicloud.mapper.UserMapper;
import com.lcyy.aicloud.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lcyy.aicloud.util.ImageNameUtil;
import com.lcyy.aicloud.util.ResponseEntity;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dlwlrma
 * @since 2024-10-31
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * 用户登录
     * @author dlwlrma
     * @date 2024/11/3 15:50
     * @param userDTO
     * @param request
     * @return com.lcyy.aicloud.util.ResponseEntity
     */
    @Override
    public ResponseEntity login(UserDTO userDTO, HttpServletRequest request) {
        //1.非空判断已不再需要，交给了@Validated注解来实现
        String redisCaptchaKey = ImageNameUtil.getCaptchaName(request);
        //根据key获取value
        String RedisCaptchaValue = (String) redisTemplate.opsForValue().get(redisCaptchaKey);
        if(!StringUtils.hasLength(RedisCaptchaValue) || !RedisCaptchaValue.equalsIgnoreCase(userDTO.getCaptcha())){
            return ResponseEntity.failure("验证码验证错误");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userDTO.getUsername());
        User user = this.getOne(queryWrapper);
        if(user != null && passwordEncoder.matches(userDTO.getPassword(), user.getPassword())){
            //1.生成jwt
            Map<String,Object> payLoad = new HashMap<>();
            payLoad.put("uid", user.getUid());
            payLoad.put("username", user.getUsername());
            //返回给前端一个jwt和用户名
            Map<String,Object> result = new HashMap<>();
            result.put("jwt", JWTUtil.createToken(payLoad, jwtSecret.getBytes()));
            result.put("username", user.getUsername());
            return ResponseEntity.success(result);
        }
        return ResponseEntity.failure("用户名或密码错误");
    }

    /**
     * 用户注册
     * @author dlwlrma
     * @date 2024/11/3 15:50
     * @param user
     * @return com.lcyy.aicloud.util.ResponseEntity
     */
    @Override
    public ResponseEntity add(User user) {
        //1.将明文密码进行加盐处理
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        boolean result = this.save(user);
        if(result){
            return ResponseEntity.success("注册成功" + result);
        }else {
            return ResponseEntity.failure("注册失败");
        }
    }
}

package com.lcyy.aicloud.controller;

import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lcyy.aicloud.model.entity.User;
import com.lcyy.aicloud.model.dto.UserDTO;
import com.lcyy.aicloud.service.IUserService;
import com.lcyy.aicloud.util.ImageNameUtil;
import com.lcyy.aicloud.util.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: dlwlrma
 * @data 2024年10月26日 16:31
 * @Description 用户控制器
 */
@Tag(name = "用户控制器", description = "用户控制器")
@RequestMapping("/user")
@RestController
public class UserController {

    @Resource
    private IUserService iUserService;

    /**
     * 用户登录控制接口
     * @author dlwlrma
     * @date 2024/10/26 16:32
     * @return com.lcyy.aicloud.util.ResponseEntity
     */
    @Operation(summary = "用户登录控制接口", description = "用户登录控制接口")
    @PostMapping("/login")
    public ResponseEntity login(@Validated UserDTO userDTO, HttpServletRequest request){
        return iUserService.login(userDTO,request);
    }

    /**
     * 用户注册方法
     * @author dlwlrma
     * @date 2024/10/31 21:38
     * @param user
     * @return com.lcyy.aicloud.util.ResponseEntity
     */
    @Operation(summary = "用户注册方法", description = "用户注册方法")
    @PostMapping("/register")
    public ResponseEntity add(@Validated User user ){
        return iUserService.add(user);
    }
}

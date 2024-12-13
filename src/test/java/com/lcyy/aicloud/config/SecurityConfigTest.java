package com.lcyy.aicloud.config;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class SecurityConfigTest {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Test
    void passwordEncoder() {
        //明文密码
        String password = "1234";
        //随机加盐算法加密
        System.out.println(passwordEncoder.encode(password));
        System.out.println(passwordEncoder.encode(password));
        System.out.println(passwordEncoder.encode(password));

    }
}
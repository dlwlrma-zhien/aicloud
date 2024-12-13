package com.lcyy.aicloud.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.crypto.SecureUtil;
import com.lcyy.aicloud.service.CaptchaService;
import com.lcyy.aicloud.util.ImageNameUtil;
import com.lcyy.aicloud.util.MinIoUtil;
import com.lcyy.aicloud.util.ResponseEntity;
import io.minio.errors.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

/**
 * @author: dlwlrma
 * @data 2024年10月29日 19:12
 * @Description 验证码生成控制器
 */
@Tag(name = "验证码生成控制器", description = "验证码生成控制器")
@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    @Resource
    private CaptchaService captchaService;

    /**
     * 生成验证码
     * @author dlwlrma
     * @date 2024/10/29 19:13
     * @return null
     */
    @Operation(summary = "生成验证码", description = "生成验证码")
    @PostMapping("/create")
    public ResponseEntity createCaptcha(HttpServletRequest request) throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return captchaService.createCaptcha(request);
    }
}

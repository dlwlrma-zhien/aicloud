package com.lcyy.aicloud.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.lcyy.aicloud.service.CaptchaService;
import com.lcyy.aicloud.util.ImageNameUtil;
import com.lcyy.aicloud.util.MinIoUtil;
import com.lcyy.aicloud.util.ResponseEntity;
import io.minio.errors.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

/**
 * @author: dlwlrma
 * @data 2024年11月03日 15:53
 * @Description: TODO:
 */
@Service
public class CaptchaServiceImpl implements CaptchaService {

    @Resource
    private MinIoUtil minIoUtil;

    //导入redis
    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public ResponseEntity createCaptcha(HttpServletRequest request)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException,
            InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String url = "";
        //定义图形验证码的长和宽
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(124, 40);
        //获取地址,使用HuTool包下的工具类生成md5加密串
        String fileName = ImageNameUtil.getCaptchaName(request);

        try(InputStream inputStream = new ByteArrayInputStream(lineCaptcha.getImageBytes())){
            //将生成的验证码存放到MinIO中
            url = minIoUtil.upload(fileName, inputStream, "image/png");
            //获取验证码
            String code = lineCaptcha.getCode();
            //存放到redis中,过期时间为60秒
            redisTemplate.opsForValue().set(fileName,code,60, TimeUnit.SECONDS);
        }
        return ResponseEntity.success(url);
    }
}

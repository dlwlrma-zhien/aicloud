package com.lcyy.aicloud.util;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import io.minio.errors.*;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MinIOUtil测试类
 * @author dlwlrma
 * @date 2024/10/28 21:30
 * @return null
 */
@SpringBootTest
class MinIoUtilTest {

    @Resource
    private MinIoUtil minIoUtil;

    @Test
    void upload() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        File file = new File("d:\\IU.jpg");
        FileInputStream inputStream = new FileInputStream(file);
        System.out.println(minIoUtil.upload("IU.jpg", inputStream, "image/jpg"));
    }

    @Test
    void creatCapth(){
        //定义图形验证码的长和宽
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(124, 40);
        //图形验证码写出，可以写出到文件，也可以写出到流
        lineCaptcha.write("d:/code.png");
        System.out.println(lineCaptcha.getCode());
    }
}
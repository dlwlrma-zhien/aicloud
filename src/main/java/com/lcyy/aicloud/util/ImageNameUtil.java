package com.lcyy.aicloud.util;

import cn.hutool.crypto.SecureUtil;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author: dlwlrma
 * @data 2024年10月31日 20:35
 * @Description: TODO:
 */
public class ImageNameUtil {

    public static String getCaptchaName(HttpServletRequest request) {
        return "captcha" + SecureUtil.md5(request.getRemoteAddr());
    }
}

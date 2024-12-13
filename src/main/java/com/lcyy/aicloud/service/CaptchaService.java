package com.lcyy.aicloud.service;

import com.lcyy.aicloud.util.MinIoUtil;
import com.lcyy.aicloud.util.ResponseEntity;
import io.minio.errors.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author: dlwlrma
 * @data 2024年11月03日 15:52
 */
public interface CaptchaService {
    ResponseEntity createCaptcha(HttpServletRequest request) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
}

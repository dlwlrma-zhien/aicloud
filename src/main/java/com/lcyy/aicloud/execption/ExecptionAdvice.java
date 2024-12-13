package com.lcyy.aicloud.execption;

import com.lcyy.aicloud.util.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author: dlwlrma
 * @data 2024年10月31日 20:11
 * @Description: TODO:全局统一异常处理
 */

@RestControllerAdvice
public class ExecptionAdvice {

    /**
     * 校验异常处理,受用于@Validated注解 + Exception的子类BindException
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity handleBindException(BindException e) {
        return ResponseEntity.failure(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    /**
     * 通用异常处理
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception e) {
        return ResponseEntity.failure(e.getMessage());
    }
}

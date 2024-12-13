package com.lcyy.aicloud.util.idempotent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解实现幂等性判断
 * @author dlwlrma
 * @date 2024/11/11 17:02
 * @return null
 */
//作用在方法上
@Target(ElementType.METHOD)
//运行时其效果
@Retention(RetentionPolicy.RUNTIME)
public @interface Idemponent {

    /**
     * 请求参数的标识符，默认为requestId
     * @author dlwlrma
     * @date 2024/11/24 17:55
     * @return java.lang.String
     */
     String requestId() default "requestId";

    /**
     * 幂等性判断的时间
     * @author dlwlrma
     * @date 2024/11/11 17:10
     * @return int
     */
    int time() default 60;
}

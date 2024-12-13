package com.lcyy.aicloud.config.interceptor;

import cn.hutool.crypto.SecureUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lcyy.aicloud.model.SecurityUserDetails;
import com.lcyy.aicloud.util.SecurityUtil;
import com.lcyy.aicloud.util.idempotent.Idemponent;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.concurrent.TimeUnit;

/**
 * @author: dlwlrma
 * @data 2024年11月11日 17:20
 * @Description
 */
@Component
public class IdempontentInterceptor implements HandlerInterceptor {

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Idemponent idemponent = null;
        try{
             idemponent = ((HandlerMethod) handler).getMethod().getAnnotation(Idemponent.class);
        }catch (Exception e){
            //捕获异常
        }
        if(idemponent != null){
            //进行幂等性校验
            String id = createId(request);
            //放到redis中
            if( redisTemplate.opsForValue().get(id) != null){
                return false;
            }else{
                redisTemplate.opsForValue().set(id,"",idemponent.time(), TimeUnit.SECONDS);
                return true;
            }
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    /**
     * 生成幂等性id:(用户id + 请求参数) md5加密
     * @author dlwlrma
     * @date 2024/11/11 17:41
     * @param request
     */
    private String createId(HttpServletRequest request) throws JsonProcessingException {
        Long uid = 0L;
        SecurityUserDetails currentUserId = SecurityUtil.getCurrentUserId();
        if(currentUserId != null){
            uid = currentUserId.getUid();
        }
        String requestParameter = objectMapper.writeValueAsString(request.getParameterMap());
        return SecureUtil.md5(uid + requestParameter);
    }
}

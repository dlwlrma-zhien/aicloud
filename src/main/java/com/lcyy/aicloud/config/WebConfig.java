package com.lcyy.aicloud.config;

import com.lcyy.aicloud.config.interceptor.IdempontentInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author: dlwlrma
 * @data 2024年11月11日 18:00
 * @Description
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Resource
    private IdempontentInterceptor idempontentInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(idempontentInterceptor)
                .addPathPatterns("/**");
    }
}

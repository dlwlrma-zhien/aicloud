package com.lcyy.aicloud.config;


import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MP的分页插件配置类
 * @author dlwlrma
 * @date 2024/11/10 17:22
 * @return null
 */
@Configuration
public class PageConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 将 MP 里面的分页插件设置 MP
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }
}
package com.lcyy.aicloud.config;

/**
 * @author: dlwlrma
 * @data 2024年11月01日 17:01
 * @Description: TODO:Spring Security 安全框架
 */

import com.lcyy.aicloud.config.filter.LoginAuthenticationFilter;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Resource
    private LoginAuthenticationFilter LoginAuthenticationFilter;

    /**
     * 密码加密器
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置过滤器链
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http
                //禁止前端明文验证
                .httpBasic(AbstractHttpConfigurer::disable)
                //禁止跨域攻击
                .csrf(AbstractHttpConfigurer::disable)
                //禁止默认登录页
                .formLogin(AbstractHttpConfigurer::disable)
                //禁止默认登出页
                .logout(AbstractHttpConfigurer::disable)
                //禁止iframe（支持登陆页面的加载）
                .headers(AbstractHttpConfigurer::disable)
                //禁止session(项目已采用JWT)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //放行接口
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                // 放行静态资源
                                "/layui/**",
                                "/js/**",
                                "/image/**",
                                "/admin.html",
                                "/login.html",
                                "/index.html",
                                "/register.html",
                                "/user/**",
                                "/captcha/create",
                                "/openai/**",
                                "/xunfei/**",
                                "/qianfan/**",
                                "/discuss/**",
                                "/kafka/**",
                                "/swagger-ui/**",
                                "/v3/**",
                                "/doc.html",
                                "/webjars/**",
                                "/doubao/**",
                                "/"
                        ).permitAll()
                        //其他接口都需要认证
                        .anyRequest().authenticated()
                )
                //添加自定义认证过滤器
                .addFilterBefore(LoginAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}

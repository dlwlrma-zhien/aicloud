package com.lcyy.aicloud.config.filter;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.lcyy.aicloud.model.SecurityUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * @author: dlwlrma
 * @data 2024年11月01日 18:13
 * @Description: TODO:配置自定义的用户登录过滤器
 */
@Component
public class LoginAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String secret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //1.获取JWT令牌
        String token = request.getHeader("Authorization");
        //2.判断JWT令牌是否为空，如果不为空，则判断令牌是否正确
        if(StringUtils.hasLength(token)){
            //3.令牌不为空，则验证其令牌的正确性
            if(JWTUtil.verify(token, secret.getBytes())){
                //4.令牌正确，获取用户信息
                JWT jwt  = JWTUtil.parseToken(token);
                if(jwt != null && jwt.getPayload("uid")!=null){
                    Long uid = Long.parseLong(jwt.getPayload("uid").toString());
                    String username = (String) jwt.getPayload("username");
                    SecurityUserDetails securityUserDetails = new SecurityUserDetails(uid,username,"");
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                             securityUserDetails, null, securityUserDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}

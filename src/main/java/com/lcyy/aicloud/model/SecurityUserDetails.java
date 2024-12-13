package com.lcyy.aicloud.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @author: dlwlrma
 * @data 2024年11月02日 10:42
 * @Description: TODO:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SecurityUserDetails implements UserDetails {

    private  String username;
    private  String password;
    private long uid;
    //有参构造器
    public SecurityUserDetails(Long uid,String username,String password){
        this.uid = uid;
        this.username = username;
        this.password = password;
    }

    //权限
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    //密码
    @Override
    public String getPassword() {
        return null;
    }

    //用户名
    @Override
    public String getUsername() {
        return null;
    }

    //账户是否过期
    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    //账户是否被锁定
    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    //凭证是否过期
    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    //账户是否可用
    @Override
    public boolean isEnabled() {
        return true;
    }
}

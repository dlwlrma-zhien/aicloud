package com.lcyy.aicloud.util;

import com.lcyy.aicloud.model.SecurityUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author: dlwlrma
 * @data 2024年11月02日 16:55
 * @Description: TODO:Security的工具类
 */
public class SecurityUtil {
    public static SecurityUserDetails getCurrentUserId(){
        SecurityUserDetails userDetails = null;
        try{
            userDetails = (SecurityUserDetails) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
        }catch (Exception e){

        }
        return userDetails;
    }
}

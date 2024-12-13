package com.lcyy.aicloud.model.dto;

import com.lcyy.aicloud.model.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author: dlwlrma
 * @data 2024年10月31日 20:29
 * @Description: TODO: 前端传给后端的验证码DTO
 */
@Data
public class UserDTO extends User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "验证码不能为空")
    private String captcha;
}

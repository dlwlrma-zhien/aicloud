package com.lcyy.aicloud.service;

import com.lcyy.aicloud.model.dto.UserDTO;
import com.lcyy.aicloud.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lcyy.aicloud.util.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dlwlrma
 * @since 2024-10-31
 */
public interface IUserService extends IService<User> {
    ResponseEntity login(UserDTO userDTO, HttpServletRequest request);

    ResponseEntity add(User user);
}

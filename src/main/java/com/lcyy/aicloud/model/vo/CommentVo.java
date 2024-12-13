package com.lcyy.aicloud.model.vo;

import com.lcyy.aicloud.model.entity.Comment;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author: dlwlrma
 * @data 2024年11月08日 18:12
 * @Description: TODO:返回给前端的comment对象
 */
@Data
public class CommentVo extends Comment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String username;
}

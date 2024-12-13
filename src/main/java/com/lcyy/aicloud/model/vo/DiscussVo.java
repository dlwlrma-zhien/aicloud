package com.lcyy.aicloud.model.vo;

import com.lcyy.aicloud.model.entity.Discuss;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author: dlwlrma
 * @data 2024年11月08日 18:10
 * @Description: TODO:返回给前端discuss的对象
 */
@Data
public class DiscussVo extends Discuss implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String username;;
}

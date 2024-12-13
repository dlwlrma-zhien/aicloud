package com.lcyy.aicloud.util;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author: dlwlrma
 * @data 2024年10月25日 20:56
 * @Description: TODO:统一响应格式给前端
 */
@Data
public class ResponseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    //状态码
    private Integer code;

    //状态信息
    private String msg;

    //返回的数据
    private Object data;

    /**
     * 返回成功的信息封装
     * @author dlwlrma
     * @date 2024/10/25 21:00
     * @param data
     * @return com.lcyy.aicloud.util.ResponseEntity
     */
    public static ResponseEntity success(Object data){
        ResponseEntity responseEntity = new ResponseEntity();
        responseEntity.setCode(200);
        responseEntity.setMsg("success");
        responseEntity.setData(data);
        return responseEntity;
    }

    /**
     * 返回失败的信息封装
     * @author dlwlrma
     * @date 2024/10/25 21:01
     * @param
     * @return com.lcyy.aicloud.util.ResponseEntity
     */
    public static ResponseEntity failure(String msg){
        ResponseEntity responseEntity = new ResponseEntity();
        responseEntity.setCode(500);
        responseEntity.setMsg(msg);
        responseEntity.setData(null);
        return responseEntity;
    }
}

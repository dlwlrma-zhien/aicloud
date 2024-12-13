package com.lcyy.aicloud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lcyy.aicloud.model.entity.Answer;
import com.lcyy.aicloud.util.ResponseEntity;

/**
 * @author: dlwlrma
 * @data 2024年11月06日 22:06
 */
public interface QianfanService extends IService<Answer>{
    ResponseEntity chatQuestion(String question);

    ResponseEntity getChatList();

    ResponseEntity drawQuestion(String question) throws Exception;

    ResponseEntity getDrawList();
}

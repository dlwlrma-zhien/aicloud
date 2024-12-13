package com.lcyy.aicloud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.lcyy.aicloud.model.entity.Answer;
import com.lcyy.aicloud.util.ResponseEntity;

/**
 * @author: dlwlrma
 * @data 2024年11月05日 20:29
 */
public interface XunfeiService  extends IService<Answer> {
    ResponseEntity chat(String question) throws JsonProcessingException;

    ResponseEntity getChatList();

    ResponseEntity draw(String question) throws Exception;

    ResponseEntity drawList();
}

package com.lcyy.aicloud.service;

import com.lcyy.aicloud.model.entity.Answer;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lcyy.aicloud.util.ResponseEntity;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dlwlrma
 * @since 2024-11-02
 */
public interface OpenAIService extends IService<Answer> {

    ResponseEntity chat(String question);

    ResponseEntity draw(String question);

    ResponseEntity chatList();

    ResponseEntity drawList();
}

package com.lcyy.aicloud.controller.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lcyy.aicloud.service.XunfeiService;
import com.lcyy.aicloud.util.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: dlwlrma
 * @data 2024年11月04日 18:35
 * @Description 讯飞大模型接口
 */
@Tag(name = "讯飞大模型接口", description = "讯飞大模型接口")
@RestController
@RequestMapping("/xunfei")
public class XunfeiController {

    @Resource
    private XunfeiService xunfeiService;

    /**
     * 讯飞大模型对话接口
     * @param question
     * @return
     */
    @Operation(summary = "讯飞大模型对话接口", description = "讯飞大模型对话接口")
    @PostMapping("/chat/question")
    public ResponseEntity chat(String question) throws JsonProcessingException {
        return xunfeiService.chat(question);
    }

    /**
     * 获取对话历史记录接口
     * @author dlwlrma
     * @date 2024/11/6 15:15
     * @return com.lcyy.aicloud.util.ResponseEntity
     */
    @Operation(summary = "获取对话历史记录接口", description = "获取对话历史记录接口")
    @GetMapping("/getchatlist")
    public ResponseEntity getChatList() {
        return xunfeiService.getChatList();
    }

    /**
     * 获取绘图接口
     * @author dlwlrma
     * @date 2024/11/6 15:54
     * @param question
     * @return com.lcyy.aicloud.util.ResponseEntity
     */
    @Operation(summary = "获取绘图接口", description = "获取绘图接口")
    @PostMapping("/draw/question")
    public ResponseEntity draw(String question) throws Exception {
        return xunfeiService.draw(question);
    }

    /**
     * 获取绘图历史记录接口
     * @author dlwlrma
     * @date 2024/11/7 22:27
     * @return com.lcyy.aicloud.util.ResponseEntity
     */
    @Operation(summary = "获取绘图历史记录接口", description = "获取绘图历史记录接口")
    @GetMapping("/getdrawlist")
    public ResponseEntity getDrawList() {
        return xunfeiService.drawList();
    }
}

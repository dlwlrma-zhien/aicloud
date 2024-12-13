package com.lcyy.aicloud.controller.ai;

import com.lcyy.aicloud.service.QianfanService;
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
 * @data 2024年11月06日 22:05
 * @Description 百度千帆大模型接口
 */
@Tag(name = "百度千帆大模型接口", description = "百度千帆大模型接口")
@RestController
@RequestMapping("/qianfan")
public class QianfanController {

    @Resource
    private QianfanService qianfanService;

    /**
     * 获取千帆大模型对话
     * @author dlwlrma
     * @date 2024/11/6 23:03
     * @param question
     * @return com.lcyy.aicloud.util.ResponseEntity
     */
    @Operation(summary = "获取千帆大模型对话", description = "获取千帆大模型对话")
    @PostMapping("/chat/question")
    public ResponseEntity chatQuestion(String question){
        return qianfanService.chatQuestion(question);
    }


    /**
     * 获取千帆大模型历史对话记录
     * @author dlwlrma
     * @date 2024/11/7 22:26
     * @return com.lcyy.aicloud.util.ResponseEntity
     */
    @Operation(summary = "获取千帆大模型历史对话记录", description = "获取千帆大模型历史对话记录")
    @GetMapping("/getchatList")
    public ResponseEntity getChatList(){
        return qianfanService.getChatList();
    }

    /**
     * 获取千帆大模型绘画
     * @author dlwlrma
     * @date 2024/11/7 22:26
     * @param question
     * @return com.lcyy.aicloud.util.ResponseEntity
     */
    @Operation(summary = "获取千帆大模型绘画", description = "获取千帆大模型绘画")
    @PostMapping("/draw/question")
    public ResponseEntity drawQuestion(String question) throws Exception {
        return qianfanService.drawQuestion(question);
    }

    /**
     * 获取千帆大模型绘画历史记录
     * @author dlwlrma
     * @date 2024/11/7 22:27
     * @return com.lcyy.aicloud.util.ResponseEntity
     */
    @Operation(summary = "获取千帆大模型绘画历史记录", description = "获取千帆大模型绘画历史记录")
    @GetMapping("/getdrawlist")
    public ResponseEntity getDrawList(){
        return qianfanService.getDrawList();
    }
}

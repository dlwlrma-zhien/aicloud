package com.lcyy.aicloud.controller.ai;

import com.lcyy.aicloud.service.OpenAIService;
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
 * @data 2024年10月25日 18:05
 * @Description openAI接口调用控制器
 */

@Tag(name = "openAI接口调用控制器", description = "openAI接口调用控制器")
@RestController
@RequestMapping("/openai")
public class OpenAIController {

    //注入答案服务
    @Resource
    private OpenAIService OpenAIService;

    /**
     * 调用OpenAI聊天接口
     * @author dlwlrma
     * @date 2024/10/25 18:11
     * @return java.lang.String
     */
    @Operation(summary = "调用OpenAI聊天接口", description = "调用OpenAI聊天接口")
    @PostMapping("/chat/question")
    public ResponseEntity chat(String question) {
        return OpenAIService.chat(question);
    }

    /**
     * 调用OpenAI绘图接口
     * @author dlwlrma
     * @date 2024/10/26 11:00
     * @param question
     * @return com.lcyy.aicloud.util.ResponseEntity
     */
    @Operation(summary = "调用OpenAI绘图接口", description = "调用OpenAI绘图接口")
    @PostMapping("/draw/question")
    public ResponseEntity draw(String question) {
        return OpenAIService.draw(question);
    }

    /**
     * 获取聊天记录
     * @author dlwlrma
     * @date 2024/10/26 11:01
     * @return com.lcyy.aicloud.util.ResponseEntity
     */
    @Operation(summary = "获取聊天记录", description = "获取聊天记录")
    @GetMapping("/getchatlist")
    public ResponseEntity getChatList(){
        return OpenAIService.chatList();
    }

    /**
     * 获取绘图记录
     * @author dlwlrma
     * @date 2024/11/3 14:18
     * @return com.lcyy.aicloud.util.ResponseEntity
     */
    @Operation(summary = "获取绘图记录", description = "获取绘图记录")
    @GetMapping("/getdrawlist")
    public ResponseEntity getDrawList(){
        return OpenAIService.drawList();
    }
}

package com.lcyy.aicloud.controller.ai;

import com.lcyy.aicloud.service.DoubaoService;
import com.lcyy.aicloud.util.ResponseEntity;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: dlwlrma
 * @data 2024年11月12日 20:21
 * @Description 豆包大模型接口
 */
@RestController
@RequestMapping("/doubao")
public class DoubaoController {


    @Resource
    private DoubaoService doubaoService;

    @PostMapping("/chat/question")
    public ResponseEntity chat(String question) {
        return doubaoService.chat(question);
    }

    @GetMapping("/chatlist")
    public ResponseEntity chatList() {
        return doubaoService.chatList();
    }
}

package com.lcyy.aicloud.controller.topic;

import com.lcyy.aicloud.model.entity.Comment;
import com.lcyy.aicloud.service.ICommentService;
import com.lcyy.aicloud.util.ResponseEntity;
import com.lcyy.aicloud.util.idempotent.Idemponent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: dlwlrma
 * @data 2024年11月09日 17:22
 * @Description 评论接口
 */
@Tag(name = "评论接口", description = "评论接口")
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private ICommentService commentService;

    /**
     * 添加评论
     * @author dlwlrma
     * @date 2024/11/10 19:49
     * @param comment
     * @return com.lcyy.aicloud.util.ResponseEntity
     */
    @Operation(summary = "添加评论", description = "添加评论")
    @PostMapping("/add")
    @Idemponent//幂等性判断，在60秒内防止一个用户多次重复提交
    private ResponseEntity addComment(@Validated Comment comment){
        return commentService.addComment(comment);
    }
}

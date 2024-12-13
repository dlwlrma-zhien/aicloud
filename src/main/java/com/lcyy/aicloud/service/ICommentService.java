package com.lcyy.aicloud.service;

import com.lcyy.aicloud.model.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lcyy.aicloud.model.vo.CommentVo;
import com.lcyy.aicloud.util.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dlwlrma
 * @since 2024-11-07
 */
public interface ICommentService extends IService<Comment> {
    List<CommentVo> getCommentList(Long did);

    ResponseEntity addComment(Comment comment);
}

package com.lcyy.aicloud.service.impl;

import com.lcyy.aicloud.model.entity.Comment;
import com.lcyy.aicloud.mapper.CommentMapper;
import com.lcyy.aicloud.model.vo.CommentVo;
import com.lcyy.aicloud.service.ICommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lcyy.aicloud.util.ResponseEntity;
import com.lcyy.aicloud.util.SecurityUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dlwlrma
 * @since 2024-11-07
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

    @Resource
    private CommentMapper commentMapper;

    @Override
    public List<CommentVo> getCommentList(Long did) {
        return commentMapper.getCommentList(did);
    }

    @Override
    public ResponseEntity addComment(Comment comment) {
        comment.setUid(SecurityUtil.getCurrentUserId().getUid());
        boolean saveResult = this.save(comment);
        if(saveResult){
            return ResponseEntity.success(saveResult);
        }
        return ResponseEntity.failure("评论失败");
    }
}

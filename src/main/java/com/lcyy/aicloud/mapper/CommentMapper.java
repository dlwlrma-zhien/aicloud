package com.lcyy.aicloud.mapper;

import com.lcyy.aicloud.model.entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lcyy.aicloud.model.vo.CommentVo;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author dlwlrma
 * @since 2024-11-07
 */
public interface CommentMapper extends BaseMapper<Comment> {
    @Select("SELECT c.*,u.username FROM `comment` c left join `user` u on u.uid=c.uid " +
            "where c.did=#{did} order by c.cid desc")
    List<CommentVo> getCommentList(@RequestParam("did") Long did);
}

package com.lcyy.aicloud.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author dlwlrma
 * @since 2024-11-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "cid", type = IdType.AUTO)
    private Long cid;

    /**
     * 评论内容
     */
    @NotNull(message = "评论内容不能为空")
    private String content;

    /**
     * 评论用户id
     */
    private Long uid;

    /**
     * 讨论id
     */
    @NotNull(message = "讨论id不能为空")
    private Long did;

    /**
     * 回复id,0=顶级评论id
     */
    private Long pid;

    /**
     * 创建时间
     */
    private LocalDateTime createtime;

    /**
     * 修改时间
     */
    private LocalDateTime updatetime;

    /**
     * 点赞数
     */
    private Integer supportcount;

    /**
     * 状态：预留字段
     */
    private Integer state;


}

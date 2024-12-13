package com.lcyy.aicloud.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author dlwlrma
 * @since 2024-11-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("answer")
@NoArgsConstructor
@AllArgsConstructor
public class Answer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "aid", type = IdType.AUTO)
    private Long aid;

    /**
     * 问题
     */
    private String title;

    /**
     * 答案
     */
    private String content;

    /**
     * 类型：1：openai；2：通义千问；3：讯飞星火；4：文心一言；5：豆包；6：本地大模型
     */
    private Integer modle;

    /**
     * 创建时间
     */
    private LocalDateTime createtime;

    /**
     * 更新时间
     */
    private LocalDateTime updatetime;

    /**
     * 用户id
     */
    private Long uid;

    /**
     * 1：对话；2：图片
     */
    private Integer type;


}

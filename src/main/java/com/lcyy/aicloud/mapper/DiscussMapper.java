package com.lcyy.aicloud.mapper;

import com.lcyy.aicloud.model.entity.Discuss;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author dlwlrma
 * @since 2024-11-07
 */
public interface DiscussMapper extends BaseMapper<Discuss> {

    @Update("update discuss set readcount = readcount + 1 where did = #{did}")
    int updateReadCountInt(@RequestParam("did") Long did);

    @Update("update discuss set supportcount = supportcount + 1 where did = #{did}")
    int updateSupportCountInt(@RequestParam("did") Long did);

}

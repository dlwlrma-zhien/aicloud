package com.lcyy.aicloud.controller.topic;

import com.lcyy.aicloud.model.entity.Discuss;
import com.lcyy.aicloud.service.IDiscussService;
import com.lcyy.aicloud.util.ResponseEntity;
import com.lcyy.aicloud.util.idempotent.Idemponent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

/**
 * @author: dlwlrma
 * @data 2024年11月07日 19:23
 * @Description 话题讨论接口
 */
@Tag(name = "话题讨论接口", description = "话题讨论接口")
@RestController
@RequestMapping("/discuss")
public class DiscussController {

    @Resource
    private IDiscussService discussService;

    /**
     * 添加话题
     * @author dlwlrma
     * @date 2024/11/7 20:50
     * @param discuss
     * @return com.lcyy.aicloud.util.ResponseEntity
     */
    @Operation(summary = "添加话题", description = "添加话题")
    @PostMapping("/add")
    @Idemponent
    public ResponseEntity addDiscuss(@Validated Discuss discuss){
        return discussService.addDiscuss(discuss);
    }

    /**
     * 获取我的话题列表
     * @author dlwlrma
     * @date 2024/11/7 21:52
     * @return com.lcyy.aicloud.util.ResponseEntity
     */
    @Operation(summary = "获取我的话题列表", description = "获取我的话题列表")
    @GetMapping("/mylist")
    public ResponseEntity getMyList(){
        return discussService.getMyList();
    }

    /**
     * 删除我的话题
     * @author dlwlrma
     * @date 2024/11/7 21:55
     * @param did
     * @return com.lcyy.aicloud.util.ResponseEntity
     */
    @Operation(summary = "删除我的话题", description = "删除我的话题")
    @DeleteMapping("/delete")
    public ResponseEntity delete(Long did){
        return discussService.delete(did);
    }

    /**
     * 话题详情
     * @author dlwlrma
     * @date 2024/11/10 17:37
     * @param did
     * @return com.lcyy.aicloud.util.ResponseEntity
     */
    @Operation(summary = "话题详情", description = "话题详情")
    @GetMapping("/detail")
    public ResponseEntity detail(Long did) throws ExecutionException, InterruptedException {
        return discussService.detail(did);
    }

    /**
     * 点赞
     * @author dlwlrma
     * @date 2024/11/10 17:37
     * @param did
     * @return com.lcyy.aicloud.util.ResponseEntity
     */
    @Operation(summary = "点赞", description = "点赞")
    @PostMapping("/doSupport")
    public ResponseEntity doSupport(Long did){
        return discussService.doSupport(did);
    }

    /**
     * 我的话题列表带分页
     * @author dlwlrma
     * @date 2024/11/10 17:35
     * @param page
     * @param type ：1.表示推荐（按照点赞数量排序） 2.表示最新（按照发表时间最新开始排序）
     * @return com.lcyy.aicloud.util.ResponseEntity
     */
    @Operation(summary = "我的话题列表带分页", description = "我的话题列表带分页")
    @GetMapping("/list")
    public ResponseEntity listPage(Integer page,Integer type){
        return discussService.listPage(page,type);
    }
}

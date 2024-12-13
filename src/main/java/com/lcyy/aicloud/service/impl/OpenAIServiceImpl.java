package com.lcyy.aicloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lcyy.aicloud.model.entity.Answer;
import com.lcyy.aicloud.mapper.AnswerMapper;
import com.lcyy.aicloud.model.enums.AiModelEnum;
import com.lcyy.aicloud.model.enums.AiTypeEnum;
import com.lcyy.aicloud.service.OpenAIService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lcyy.aicloud.util.AppVariableUtil;
import com.lcyy.aicloud.util.ResponseEntity;
import com.lcyy.aicloud.util.SecurityUtil;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.DAYS;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dlwlrma
 * @since 2024-11-02
 */
@Service
public class OpenAIServiceImpl extends ServiceImpl<AnswerMapper, Answer> implements OpenAIService {

    //将对话模型注入进来
    @Resource
    private OpenAiChatModel chatModel;

    //将绘图模型注入进来
    @Resource
    private OpenAiImageModel imageModel;

    //注入分布式锁
    @Resource
    private RedissonClient redissonClient;

    //注入redis
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public ResponseEntity chat(String question) {
                //先判断是否为空，使用spring框架提供的工具类（StringUtils）
        if(!StringUtils.hasLength(question)){
            //输入的问题为空，则直接进行返回
            return ResponseEntity.failure("输入的问题为空");
        }
        String result = "";
        boolean addResult = false;
        //分布式锁的判断
        long uid = SecurityUtil.getCurrentUserId().getUid();
        String lockKey = AppVariableUtil.getModelLockKey(uid,
                AiModelEnum.OPENAI.getValue(),AiTypeEnum.CHAT.getValue());
        //1.获取分布式锁实例
        RLock lock = redissonClient.getLock(lockKey);
        try {
            //2.尝试获取锁
            boolean isLock = lock.tryLock(30, TimeUnit.SECONDS);
            if (!isLock) {
                return ResponseEntity.failure("请勿频繁提问，请稍后再试");
            }
            //直接调用api接口
            result = chatModel.call(question);
            //将结果存放到数据库中
            addResult = this.save(new Answer()
                    .setContent(result)
                    .setTitle(question)
                    .setType(AiTypeEnum.CHAT.getValue())
                    .setModle(AiModelEnum.OPENAI.getValue())
                    .setUid(uid));
        }catch (Exception e){

        }finally {
            lock.unlock();
        }
        if(addResult){
            return ResponseEntity.success(result);
        }
        return ResponseEntity.failure("数据保存失败，请重试!");
    }

    @Override
    public ResponseEntity draw(String question) {
        if(!StringUtils.hasLength(question)){
            return ResponseEntity.failure("输入的问题为空");
        }
        String imgUrl = "";
        boolean addResult = false;
        long uid = SecurityUtil.getCurrentUserId().getUid();
        String lockKey = AppVariableUtil.getModelLockKey(uid,
                AiModelEnum.OPENAI.getValue(),AiTypeEnum.DRAW.getValue());
        RLock lock = redissonClient.getLock(lockKey);
        try {
        ImageResponse result = imageModel.call(new ImagePrompt(question, OpenAiImageOptions.builder().withHeight(1024)
                .withWidth(1024).build()));
        lock.tryLock(30,TimeUnit.SECONDS);
        //将结果存放到数据库中
        imgUrl = result.getResult().getOutput().getUrl();
        addResult = this.save(new Answer()
                .setContent(imgUrl)
                .setTitle(question)
                .setType(AiTypeEnum.DRAW.getValue())
                .setModle(AiModelEnum.OPENAI.getValue())
                .setUid(uid));
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
        if(addResult){
            return ResponseEntity.success(imgUrl);
        }
        return ResponseEntity.failure("数据保存失败，请重试!");
    }

    /**
     * 获取历史聊天记录
     * @author dlwlrma
     * @date 2024/11/3 13:49
     * @return java.lang.Object
     */
    @Override
    public ResponseEntity chatList() {
        //获取用户id
        long uid = SecurityUtil.getCurrentUserId().getUid();
        Integer type = AiTypeEnum.CHAT.getValue();
        Integer modle = AiModelEnum.OPENAI.getValue();
        String cacheKey = AppVariableUtil.getListCacheKey(uid,modle,type);
        Object list = redisTemplate.opsForValue().get(cacheKey);
        if(list == null){
            //缓存key不存在，需要从数据库中获取
            QueryWrapper<Answer> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("uid",uid);
            queryWrapper.eq("type",type);
            queryWrapper.eq("modle",modle);
            //根据时间倒序排序
            queryWrapper.orderByDesc("aid");
            List<Answer> dataList = this.list(queryWrapper);
            //从数据库查询后存放到redis中
            redisTemplate.opsForValue().set(cacheKey,dataList,1,TimeUnit.DAYS);
            //返回结果
            return ResponseEntity.success(dataList);
        }else{
            //缓存存在,则从缓存中获取
            return ResponseEntity.success(list);
        }
    }

    /**
     * 获取历史绘画记录
     * @author dlwlrma
     * @date 2024/11/3 14:17
     * @return java.lang.Object
     */
    @Override
    public ResponseEntity drawList() {
        //获取用户id
        long uid = SecurityUtil.getCurrentUserId().getUid();
        Integer type = AiTypeEnum.DRAW.getValue();
        Integer modle = AiModelEnum.OPENAI.getValue();
        String cacheKey = AppVariableUtil.getListCacheKey(uid,modle,type);
        Object list = redisTemplate.opsForValue().get(cacheKey);
        if(list == null){
            //缓存key不存在，需要从数据库中获取
            QueryWrapper<Answer> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("uid",uid);
            queryWrapper.eq("type",type);
            queryWrapper.eq("modle",modle);
            //根据时间倒序排序
            queryWrapper.orderByDesc("aid");
            List<Answer> dataList = this.list(queryWrapper);
            //从数据库查询后存放到redis中
            redisTemplate.opsForValue().set(cacheKey,dataList,1,TimeUnit.DAYS);
            //返回结果
            return ResponseEntity.success(dataList);
        }else{
            //返回结果
            return ResponseEntity.success(list);
        }
    }
}

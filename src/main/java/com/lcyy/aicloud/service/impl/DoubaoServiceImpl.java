package com.lcyy.aicloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lcyy.aicloud.mapper.AnswerMapper;
import com.lcyy.aicloud.model.entity.Answer;
import com.lcyy.aicloud.model.enums.AiModelEnum;
import com.lcyy.aicloud.model.enums.AiTypeEnum;
import com.lcyy.aicloud.service.DoubaoService;
import com.lcyy.aicloud.util.AppVariableUtil;
import com.lcyy.aicloud.util.ResponseEntity;
import com.lcyy.aicloud.util.SecurityUtil;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: dlwlrma
 * @data 2024年11月12日 20:26
 * @Description
 */
@Service
public class DoubaoServiceImpl extends ServiceImpl<AnswerMapper, Answer> implements DoubaoService {

    @Value("${doubao.api-key}")
    private String apiKey;
    @Value("${doubao.url}")
    private String url;
    @Value("${doubao.model-id}")
    private String modelId;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Override
    public ResponseEntity chat(String question) {
       if(!StringUtils.hasLength(question)){
           return ResponseEntity.failure("输入的问题为空");
       }
       boolean addResult = false;
       String result = "";
       long uid = SecurityUtil.getCurrentUserId().getUid();
       String lockKey = AppVariableUtil.getModelLockKey(uid,
                AiModelEnum.DOUBAO.getValue(),AiTypeEnum.CHAT.getValue());
        //1.获取分布式锁实例
       RLock lock = redissonClient.getLock(lockKey);
       try {
           boolean isLock = lock.tryLock(30, TimeUnit.SECONDS);
           if (!isLock) {
               return ResponseEntity.failure("请勿频繁提问，请稍后再试");
           }
           //1.调用apikey,url地址
           ArkService service = ArkService.builder().apiKey(apiKey)
                   .baseUrl(url).build();

           //2.构建对象
           List<ChatMessage> messages = new ArrayList<>();
           messages.add(ChatMessage.builder()
                   .content(question)
                   .role(ChatMessageRole.USER)
                   .build());
           //3.创建对象进行对话
           ChatCompletionRequest request = ChatCompletionRequest.builder()
                   .model(modelId)
                   .messages(messages)
                   .build();
           //4.得到返回的结果
           result = service.createChatCompletion(request)
                   .getChoices()
                   .get(0)
                   .getMessage()
                   .getContent().toString();
           Answer answer = new Answer()
                   .setModle(AiModelEnum.DOUBAO.getValue())
                   .setType(AiTypeEnum.CHAT.getValue())
                   .setContent(result)
                   .setTitle(question)
                   .setUid(uid);
           addResult = this.save(answer);
       }catch (Exception e){
           System.out.println("通知相关程序开发人员");
       }finally {
           lock.unlock();
       }
        if(addResult){
            return ResponseEntity.success(result);
        }
       return ResponseEntity.failure("数据保存失败，请重试");
    }

    @Override
    public ResponseEntity chatList() {
        long uid = SecurityUtil.getCurrentUserId().getUid();
        Integer type = AiTypeEnum.CHAT.getValue();
        Integer modle = AiModelEnum.DOUBAO.getValue();
        String cacheKey = AppVariableUtil.getListCacheKey(uid, modle, type);
        Object list = redisTemplate.opsForValue().get(cacheKey);
        if (list == null) {
            QueryWrapper<Answer> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("uid", uid);
            queryWrapper.eq("type", type);
            queryWrapper.eq("modle", modle);
            //根据时间倒序排序
            queryWrapper.orderByDesc("aid");
            List<Answer> dataList = this.list(queryWrapper);
            redisTemplate.opsForValue().set(cacheKey,dataList,1,TimeUnit.DAYS);
            return ResponseEntity.success(dataList);
        }else{
            return ResponseEntity.success(list);
        }
    }
}

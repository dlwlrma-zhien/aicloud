package com.lcyy.aicloud.service.impl;

import cn.hutool.http.HttpRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lcyy.aicloud.mapper.AnswerMapper;
import com.lcyy.aicloud.model.entity.Answer;
import com.lcyy.aicloud.model.enums.AiModelEnum;
import com.lcyy.aicloud.model.enums.AiTypeEnum;
import com.lcyy.aicloud.service.OpenAIService;
import com.lcyy.aicloud.service.XunfeiService;
import com.lcyy.aicloud.util.*;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author: dlwlrma
 * @data 2024年11月05日 20:30
 * @Description: TODO:讯飞大模型实现类
 */
@Service
public class XunfeiServiceImpl extends ServiceImpl<AnswerMapper, Answer> implements XunfeiService {

    @Value("${xunfei.chat.url}")
    private String chatUrl;

    @Value("${xunfei.chat.api-key}")
    private String ChatApiKey;

    @Value("${xunfei.chat.api-secret}")
    private String ChatApiSecret;

    @Value("${xunfei.draw.host-url}")
    private String HostUrl;

    @Value("${xunfei.draw.api-key}")
    private String drawApiKey;

    @Value("${xunfei.draw.api-secret}")
    private String drawApiSecret;

    @Value("${xunfei.draw.app-id}")
    private String drawAppId;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private MinIoUtil minIoUtil;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Override
    public ResponseEntity chat(String question) throws JsonProcessingException {
        if(!StringUtils.hasLength(question)){
            return ResponseEntity.failure("输入的问题为空");
        }
        String content = "";
        boolean save = false;
        long uid = SecurityUtil.getCurrentUserId().getUid();
        String lockKey = AppVariableUtil.getModelLockKey(uid,
                AiModelEnum.XUNFEIXINGHUO.getValue(),AiTypeEnum.CHAT.getValue());
        //1.获取分布式锁实例
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean isLock = lock.tryLock(30, TimeUnit.SECONDS);
            if (!isLock) {
                return ResponseEntity.failure("请勿频繁提问，请稍后再试");
            }
            String bodyJson = "{\n" +
                    "    \"model\":\"generalv3.5\",\n" +
                    "    \"messages\": [\n" +
                    "        {\n" +
                    "            \"role\": \"user\",\n" +
                    "            \"content\": \"" + question + "\"\n" +
                    "        }\n" +
                    "    ],\n" +
                    "    \"stream\": false\n" +
                    "}";
            //从讯飞大模型获取原始的数据
            String Result = HttpRequest.post(chatUrl)
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + ChatApiKey + ":" + ChatApiSecret)
                    .body(bodyJson)
                    .execute()
                    .body();
            //解析原始数据中只有的回答答案
            HashMap<String, Object> resultMap = objectMapper.readValue(Result, HashMap.class);
            if (!resultMap.get("code").toString().equals("0")) {
                return ResponseEntity.failure(resultMap.get("message").toString());
            }
            ArrayList choices = (ArrayList) resultMap.get("choices");
            LinkedHashMap<String, Object> choiceMap = (LinkedHashMap<String, Object>) choices.get(0);
            LinkedHashMap<String, Object> messsageMap = (LinkedHashMap<String, Object>) choiceMap.get("message");
            content = messsageMap.get("content").toString();
            //将解析的数据保存到数据库中
            Answer answer = new Answer();
            answer.setContent(content)
                    .setTitle(question)
                    .setType(AiTypeEnum.CHAT.getValue())
                    .setModle(AiModelEnum.XUNFEIXINGHUO.getValue())
                    .setUid(uid);
            save = this.save(answer);
        }catch (Exception e){
            return ResponseEntity.failure("请求讯飞大模型失败，请重试");
        }
        finally {
            lock.unlock();
        }
        if(save){
            return ResponseEntity.success(content);
        }
        return ResponseEntity.failure("数据保存失败，请重试!");
    }

    @Override
    public ResponseEntity getChatList() {
        long uid = SecurityUtil.getCurrentUserId().getUid();
        Integer type = AiTypeEnum.CHAT.getValue();
        Integer modle = AiModelEnum.XUNFEIXINGHUO.getValue();
        String cacheKey = AppVariableUtil.getListCacheKey(uid, modle, type);
        Object list = redisTemplate.opsForValue().get(cacheKey);
        if (list == null) {
            //获取数据库中的信息
            QueryWrapper<Answer> wrapper = new QueryWrapper<>();
            wrapper.eq("uid", uid);
            wrapper.eq("type", type);
            wrapper.eq("modle", modle);
            wrapper.orderByDesc("aid");
            List<Answer> dataList = this.list(wrapper);
            redisTemplate.opsForValue().set(cacheKey, dataList, 1, TimeUnit.DAYS);
            return ResponseEntity.success(dataList);
        }else{
            return ResponseEntity.success(list);
        }
    }

    @Override
    public ResponseEntity draw(String question) throws Exception {
        if (!StringUtils.hasLength(question)) {
            return ResponseEntity.failure("输入的问题为空");
        }
        boolean saveResult = false;
        String imgUrl = "";
        long uid = SecurityUtil.getCurrentUserId().getUid();
        String lockKey = AppVariableUtil.getModelLockKey(uid,
                AiModelEnum.XUNFEIXINGHUO.getValue(),AiTypeEnum.DRAW.getValue());
        //1.获取分布式锁实例
        RLock lock = redissonClient.getLock(lockKey);
        try{
            boolean isLock = lock.tryLock(30, TimeUnit.SECONDS);
            if (!isLock) {
                return ResponseEntity.failure("请勿频繁提问，请稍后再试");
            }
            String url = AuthUrlUtil.getAuthUrl(HostUrl, drawApiKey, drawApiSecret);
            String json = "{\n" +
                "  \"header\": {\n" +
                "    \"app_id\": \"" + drawAppId + "\"\n" +
                "    },\n" +
                "  \"parameter\": {\n" +
                "    \"chat\": {\n" +
                "      \"domain\": \"general\",\n" +
                "      \"width\": 512,\n" +
                "      \"height\": 512\n" +
                "      }\n" +
                "    },\n" +
                "  \"payload\": {\n" +
                "    \"message\": {\n" +
                "      \"text\": [\n" +
                "        {\n" +
                "          \"role\": \"user\",\n" +
                "          \"content\": \"" + question + "\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  }\n" +
                "}";
            String result = HttpRequest.post(url)
                .body(json)
                .execute()
                .body();

        //结果转换只得到content
        HashMap<String, Object> resultMap = objectMapper.readValue(result, HashMap.class);
        LinkedHashMap<String, Object> payload = (LinkedHashMap<String, Object>) resultMap.get("payload");
        LinkedHashMap<String, Object> choices = (LinkedHashMap<String, Object>) payload.get("choices");
        ArrayList<LinkedHashMap<String, Object>> text = (ArrayList<LinkedHashMap<String, Object>>) choices.get("text");
        LinkedHashMap<String, Object> textMap = text.get(0);
        String content = textMap.get("content").toString();

        //解密base64-->MinIO
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(content))) {
            String imgName = "xf-draw-" + UUID.randomUUID().toString()
                    .replace("-", "");
            imgUrl = minIoUtil.upload(imgName, inputStream, "image/png");
        }

        //将数据保存到数据库中
        Answer answer = new Answer();
        answer.setContent(imgUrl)
                .setTitle(question)
                .setType(AiTypeEnum.DRAW.getValue())
                .setModle(AiModelEnum.XUNFEIXINGHUO.getValue())
                .setUid(SecurityUtil.getCurrentUserId().getUid());
        saveResult = this.save(answer);
        }catch (Exception e){
            return ResponseEntity.failure("请求讯飞大模型失败，请重试");
        }finally {
            lock.unlock();
        }
        if(saveResult){
            return ResponseEntity.success(imgUrl);
        }
       return ResponseEntity.failure("数据保存失败，请重试!");
    }

    @Override
    public ResponseEntity drawList() {
        long uid = SecurityUtil.getCurrentUserId().getUid();
        Integer type = AiTypeEnum.DRAW.getValue();
        Integer modle = AiModelEnum.XUNFEIXINGHUO.getValue();
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
            //返回结果
            return ResponseEntity.success(dataList);
        }else{
            return ResponseEntity.success(list);
        }
    }
}

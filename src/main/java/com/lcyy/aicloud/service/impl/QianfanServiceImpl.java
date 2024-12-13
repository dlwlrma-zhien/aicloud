package com.lcyy.aicloud.service.impl;

import com.baidubce.qianfan.Qianfan;
import com.baidubce.qianfan.model.chat.ChatResponse;
import com.baidubce.qianfan.model.image.Text2ImageResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lcyy.aicloud.mapper.AnswerMapper;
import com.lcyy.aicloud.model.entity.Answer;
import com.lcyy.aicloud.model.enums.AiModelEnum;
import com.lcyy.aicloud.model.enums.AiTypeEnum;
import com.lcyy.aicloud.service.OpenAIService;
import com.lcyy.aicloud.service.QianfanService;
import com.lcyy.aicloud.util.AppVariableUtil;
import com.lcyy.aicloud.util.MinIoUtil;
import com.lcyy.aicloud.util.ResponseEntity;
import com.lcyy.aicloud.util.SecurityUtil;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author: dlwlrma
 * @data 2024年11月06日 22:08
 * @Description: TODO:千帆大模型实现类
 */
@Service
public class QianfanServiceImpl extends ServiceImpl<AnswerMapper, Answer> implements QianfanService {

    @Value("${qianfan.api-key}")
    private String apiKey;
    @Value("${qianfan.secret-key}")
    private String secretKey;
    @Resource
    private MinIoUtil minIoUtil;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public ResponseEntity chatQuestion(String question) {
        if (!StringUtils.hasLength(question)) {
            return ResponseEntity.failure("输入的问题为空");
        }
        boolean save = false;
        String content = "";
        long uid = SecurityUtil.getCurrentUserId().getUid();
        String lockKey = AppVariableUtil.getModelLockKey(uid,
                AiModelEnum.WENXINYIYAN.getValue(), AiTypeEnum.CHAT.getValue());
        //1.获取分布式锁实例
        RLock lock = redissonClient.getLock(lockKey);
        try{
            //2.尝试获取锁
            boolean isLock = lock.tryLock(30, TimeUnit.SECONDS);
            if (!isLock) {
                return ResponseEntity.failure("请勿频繁提问，请稍后再试");
            }
        ChatResponse response = new Qianfan(apiKey, secretKey).chatCompletion()
                // 使用model指定预置模型
                .model("ERNIE-Speed-8K")
                // 添加用户消息 (此方法可以调用多次，以实现多轮对话的消息传递)
                .addMessage("user", question)
                .execute(); // 发起请求
        content = response.getResult();
        //将获取到的数据答案存到数据库
        Answer answer = new Answer()
                .setUid(uid)
                .setModle(AiModelEnum.WENXINYIYAN.getValue())
                .setType(AiTypeEnum.CHAT.getValue())
                .setTitle(question)
                .setContent(content);
        save = this.save(answer);
        }catch (Exception e){
            System.out.println("通知相关人员");
        }finally {
            lock.unlock();
        }
        if(save){
            return ResponseEntity.success(content);
        }
        return ResponseEntity.failure("数据保存失败，请重试");
    }

    @Override
    public ResponseEntity getChatList() {
        long uid = SecurityUtil.getCurrentUserId().getUid();
        Integer type = AiTypeEnum.CHAT.getValue();
        Integer modle = AiModelEnum.WENXINYIYAN.getValue();
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
            redisTemplate.opsForValue().set(cacheKey, dataList, 1, TimeUnit.DAYS);
            return ResponseEntity.success(dataList);
        }else{
            return ResponseEntity.success(list);
        }
    }

    @Override
    public ResponseEntity drawQuestion(String question) throws Exception {
        if(!StringUtils.hasLength(question)){
            return ResponseEntity.failure("输入的问题为空");
        }
        boolean addResult = false;
        String url = "";
        long uid = SecurityUtil.getCurrentUserId().getUid();
        String lockKey = AppVariableUtil.getModelLockKey(uid,
                AiModelEnum.WENXINYIYAN.getValue(),AiTypeEnum.DRAW.getValue());
        //1.获取分布式锁实例
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean isLock = lock.tryLock(30, TimeUnit.SECONDS);
            if (!isLock) {
                return ResponseEntity.failure("请勿频繁提问，请稍后再试");
            }
            Text2ImageResponse response = new Qianfan(apiKey, secretKey).text2Image()
                    .prompt(question)
                    .execute();
            byte[] image = response.getData().get(0).getImage();
            String fileName = "wx-" + UUID.randomUUID().toString().replace("-", "");
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(image)) {
                url = minIoUtil.upload(fileName, inputStream, "image/png");
            } catch (IOException e) {
                throw new Exception(e);
            }
            //存数据库
            Answer answer = new Answer()
                    .setContent(url)
                    .setTitle(question)
                    .setUid(uid)
                    .setType(AiTypeEnum.DRAW.getValue())
                    .setModle(AiModelEnum.WENXINYIYAN.getValue());
            addResult = this.save(answer);
        }catch (Exception e){
            System.out.println("通知相关人员");
        }finally {
            lock.unlock();
        }
        if(addResult){
            return ResponseEntity.success(url);
        }
        return ResponseEntity.failure("数据保存失败，请重试");
    }

    @Override
    public ResponseEntity getDrawList() {
        long uid = SecurityUtil.getCurrentUserId().getUid();
        Integer type = AiTypeEnum.DRAW.getValue();
        Integer modle = AiModelEnum.WENXINYIYAN.getValue();
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
            redisTemplate.opsForValue().set(cacheKey,dataList,1, TimeUnit.DAYS);
            return ResponseEntity.success(dataList);
        }else{
            return ResponseEntity.success(list);
        }
    }
}

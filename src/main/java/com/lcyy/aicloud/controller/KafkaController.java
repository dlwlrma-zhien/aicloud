package com.lcyy.aicloud.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lcyy.aicloud.util.AppVariableUtil;
import com.xxl.job.core.handler.annotation.XxlJob;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.kafka.common.internals.Topic;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @author: dlwlrma
 * @data 2024年11月10日 14:56
 * @Description kafka测试接口
 */
@Tag(name = "kafka测试接口", description = "kafka测试接口")
@RestController
@RequestMapping("/kafka")
public class KafkaController {

    @Resource
    private KafkaTemplate kafkaTemplate;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    private static final String TOPIC = "aicloud_topic";

    private static final String CANAL_TOPIC = "aicloud-anal";

    /**
     * 发送消息
     * @author dlwlrma
     * @date 2024/11/10 19:49
     * @param msg
     * @return java.lang.String
     */
    @Operation(summary = "发送消息", description = "发送消息")
    @PostMapping("/send")
    public String send(String msg) {
        if(!StringUtils.hasLength(msg)){
            return "请输入要发送的消息";
        }
        //发送消息
        kafkaTemplate.send(TOPIC, msg);
        return "success";
    }

    //消费消息,可以消费多个topic
    @KafkaListener(topics = {TOPIC})
    public void receive(String msg, Acknowledgment acknowledgment) {
        System.out.println("接收到消息：" + msg);

//        手动确认消息应答
        acknowledgment.acknowledge();
    }

    @KafkaListener(topics = {CANAL_TOPIC})
    public void cancelList(String data, Acknowledgment acknowledgment) throws JsonProcessingException {
        HashMap<String, Object> map = objectMapper.readValue(data, HashMap.class);
        if (!map.isEmpty() && map.get("database").toString().equals("aicloud") &&
                map.get("table").toString().equals("answer")) {
            // 更新 Redis 缓存
            ArrayList<LinkedHashMap<String, Object>> list =
                    (ArrayList<LinkedHashMap<String, Object>>) map.get("data");
            String cacheKey = "";
            for (LinkedHashMap<String, Object> answer : list) {
                cacheKey = AppVariableUtil.getListCacheKey(
                        Long.parseLong(answer.get("uid").toString()),
                        Integer.parseInt(answer.get("model").toString()),
                        Integer.parseInt(answer.get("type").toString()));
                redisTemplate.opsForValue().set(cacheKey, null);
            }
        }
        //手动确认消息应答
        acknowledgment.acknowledge();
    }

}

/**
 * Copyright(C) 2019 Hangzhou zhaoyunxing Technology Co., Ltd. All rights reserved.
 */
package io.github.xyz.spring.boot.kafka.service;

import com.alibaba.fastjson.JSON;
import io.github.xyz.spring.boot.kafka.entity.People;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.utils.AppInfoParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * @author zhaoyunxing
 * @date: 2019-04-28 14:29
 * @des:
 */
@Service
@Slf4j
public class KafkaServiceImpl implements KafkaService {
    private final KafkaAdmin kafkaAdmin;
    private final KafkaTemplate kafkaTemplate;

    @Autowired
    public KafkaServiceImpl(KafkaAdmin kafkaAdmin, KafkaTemplate kafkaTemplate) {
        this.kafkaAdmin = kafkaAdmin;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public String send() {
        String topic = KafkaService.TOPIC;
        // 1.判断topic是否存在,不存在创建
        createTopic(topic);
        // 2.发送信息
        String msg = JSON.toJSONString(new People(UUID.randomUUID().toString(), "sunny", 25, new Date()));
        ListenableFuture<SendResult<String, String>> listenableFuture = kafkaTemplate.send(topic, msg);

        listenableFuture.addCallback(
                // 添加消息发送结果
                result -> log.debug("push kafka:{}   {}", result.toString()),
                ex -> log.error("push kafka to [{}] failure,error message:{}", topic, ex.getMessage()));
        return topic + "send ok";
    }

    /**
     * 是否存在
     *
     * @param topic topic name
     * @return false or true
     */
    private Boolean isExistTopic(String topic) {
        AdminClient adminClient = null;
        try {
            adminClient = AdminClient.create(kafkaAdmin.getConfig());
            ListTopicsOptions listTopicsOptions = new ListTopicsOptions();
            listTopicsOptions.listInternal(true);
            ListTopicsResult res = adminClient.listTopics(listTopicsOptions);
            return res.names().get().contains(topic);
        } catch (InterruptedException | ExecutionException ex) {
            log.error("isExistTopic method has error;{}", ex.getMessage());
        } finally {
            assert adminClient != null;
            adminClient.close();
        }
        return false;
    }

    private void createTopic(String topic) {
        if (!isExistTopic(topic)) {
            log.info("kafka config: {}", kafkaAdmin.getConfig());
            AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfig());
            NewTopic newTopic = new NewTopic(topic, 4, (short) 1);
            List<NewTopic> topicList = Arrays.asList(newTopic);
            adminClient.createTopics(topicList);
            adminClient.close();
            log.info("topic {} create ok ", topic);
        }
    }
}

/**
 * Copyright(C) 2019 Hangzhou zhaoyunxing Technology Co., Ltd. All rights reserved.
 */
package io.github.xyz.spring.boot.aliyun.kafka;

import io.github.xyz.spring.boot.aliyun.kafka.listener.Listener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * @author zhaoyunxing
 * @date: 2019-04-29 20:56
 * @des:
 */
@SpringBootApplication
@EnableKafka
@Import(Listener.class)
public class AliyunKafkaServer {
    public static void main(String[] args) {
        SpringApplication.run(AliyunKafkaServer.class, args);
    }
}

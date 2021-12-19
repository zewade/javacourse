package cn.zewade.course.kafkademo;

import cn.zewade.course.kafkademo.entity.User;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zewade
 * @since  2021/12/19 17:17
 * @version 1.0
 */
@Component
public class MessagingService {

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Bean
    public NewTopic topic1() {
        return TopicBuilder.name("user_sync")
                .partitions(3)
                .replicas(1)
                .compact()
                .build();
    }

    public void sendUserMessage(String topic, User user) throws IOException {
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, JSON.toJSONString(user));
        producerRecord.headers().add("type", user.getClass().getName().getBytes(StandardCharsets.UTF_8));
        kafkaTemplate.send(producerRecord);
    }
}

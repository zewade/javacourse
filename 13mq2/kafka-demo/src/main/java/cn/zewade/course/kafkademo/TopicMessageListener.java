package cn.zewade.course.kafkademo;

import cn.zewade.course.kafkademo.entity.User;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * @author zewade
 * @version 1.0
 * @since 2021/12/19 17:40
 */
@Component
@Slf4j
public class TopicMessageListener {

    @KafkaListener(topics = "user_sync", groupId = "group1")
    public void onLoginMessage(@Payload String message, @Header("type") String type) throws Exception {
        User user = JSON.parseObject(message, getType(type));
        log.info("received user message: {}, group: {}", user, "group1");
    }

    @KafkaListener(topics = "user_sync", groupId = "group2")
    public void processLoginMessage(@Payload String message, @Header("type") String type) throws Exception {
        User user = JSON.parseObject(message, getType(type));
        log.info("received user message: {}, group: {}", user, "group2");
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> getType(String type) {
        // TODO: use cache:
        try {
            return (Class<T>) Class.forName(type);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

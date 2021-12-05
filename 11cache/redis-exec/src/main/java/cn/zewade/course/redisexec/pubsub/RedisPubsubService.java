package cn.zewade.course.redisexec.pubsub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class RedisPubsubService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private CountDownLatch latch;

    public static final String CHANNEL = "order_channel";

    /**
     * 向通道发送消息的方法
     * @param channel
     * @param message
     */
    public void sendChannelMess(String channel, String message) {
        try {
            stringRedisTemplate.convertAndSend(channel, message);
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

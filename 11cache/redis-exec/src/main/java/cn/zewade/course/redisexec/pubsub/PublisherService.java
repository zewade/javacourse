package cn.zewade.course.redisexec.pubsub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PublisherService {
    @Autowired
    private RedisPubsubService redisService;

    public String pushMsg(String params) {
        log.info(" 又开始发布消息 .......... ");
        //直接使用convertAndSend方法即可向指定的通道发布消息
        redisService.sendChannelMess(RedisPubsubService.CHANNEL,"{\"orderId\":\"1\",\"userId\":\"1\",\"productId\":\"1\",\"amount\":100}");
        return "success";
    }
}


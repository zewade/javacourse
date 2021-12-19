package cn.zewade.course.kafkademo;

import cn.zewade.course.kafkademo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.UUID;

/**
 * @author zewade
 * @version 1.0
 * @since 2021/12/19 17:54
 */
@RestController
public class TestController {

    @Autowired
    private MessagingService messagingService;

    @GetMapping("/kafka/send")
    public void sendMessage() throws IOException {
        for (int i = 1; i <= 1000; i++) {
            User user = new User();
            user.setId(i);
            user.setUserCode(UUID.randomUUID().toString());
            user.setUserName("User's Name - No." + i);
            messagingService.sendUserMessage("user_sync", user);
        }
    }
}

package cn.zewade.course.redisexec.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.concurrent.TimeUnit;


@Component
@Slf4j
public class RedisLock {

    @Resource
    private RedisTemplate redisTemplate;
    private static final Long SUCCESS = 1L;
    private long timeout = 9999; //获取锁的超时时间

    /**
     * 加锁，无阻塞
     *
     * @param
     * @param
     * @return
     */
    public Boolean tryLock(String key, String value, long expireTime) {
        Long start = System.currentTimeMillis();
        try {
            for (; ; ) {
                //SET命令返回OK ，则证明获取锁成功
                Boolean ret = redisTemplate.opsForValue().setIfAbsent(key, value, expireTime, TimeUnit.SECONDS);
                if (ret) {
                    return true;
                }
                //否则循环等待，在timeout时间内仍未获取到锁，则获取失败
                long end = System.currentTimeMillis() - start;
                if (end >= timeout) {
                    return false;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 解锁
     *
     * @param
     * @param
     * @return
     */
    public Boolean unlock(String key, String value) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        RedisScript<String> redisScript = new DefaultRedisScript<>(script, String.class);
        Object result = redisTemplate.execute(redisScript, Collections.singletonList(key), value);
        if (SUCCESS.equals(result)) {
            return true;
        }
        return false;
    }
}
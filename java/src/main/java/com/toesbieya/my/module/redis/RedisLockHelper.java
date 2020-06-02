package com.toesbieya.my.module.redis;

import com.toesbieya.my.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.Objects;

@Slf4j
public class RedisLockHelper implements LockHelper {
    private final StringRedisTemplate template = RedisUtil.getStringRedisTemplate();
    private final String key;
    private final String value;
    private final String expire;

    public RedisLockHelper(String key) {
        this.key = "lock:" + key;
        this.value = String.valueOf(System.currentTimeMillis());
        this.expire = "300";
    }

    @Override
    public void close() {
        unlock();
    }

    @Override
    public boolean lock() {
        RedisScript<Boolean> redisScript = new DefaultRedisScript<>(LockHelper.LOCK_LUA_SCRIPT, Boolean.class);
        Boolean result = template.execute(redisScript, Collections.singletonList(key), value, expire);
        return Objects.equals(result, true);
    }

    @Override
    public void unlock() {
        RedisScript<Boolean> redisScript = new DefaultRedisScript<>(LockHelper.UNLOCK_LUA_SCRIPT, Boolean.class);
        Boolean result = template.execute(redisScript, Collections.singletonList(key), value);
        if (result == null || !result) {
            log.error("redis锁释放失败，key:{},value:{}", key, value);
        }
    }
}

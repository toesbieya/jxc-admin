package com.toesbieya.my.utils;

import com.google.common.io.ByteStreams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedisUtil {
    private static String INCREMENT_AND_EXPIRE_SCRIPT = null;
    private static RedisTemplate<Object, Object> redisTemplate;

    static {
        try {
            INCREMENT_AND_EXPIRE_SCRIPT = new String(ByteStreams.toByteArray(new ClassPathResource("/script/increase_and_expire.lua").getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    public RedisUtil(RedisTemplate<Object, Object> redisTemplate) {
        RedisUtil.redisTemplate = redisTemplate;
    }

    public static boolean exist(String key) {
        Boolean result = redisTemplate.hasKey(key);
        return result != null && result;
    }

    public static long incrAndExpire(String key, int time) {
        RedisScript<Long> redisScript = new DefaultRedisScript<>(INCREMENT_AND_EXPIRE_SCRIPT, Long.class);
        Long result = redisTemplate.execute(redisScript, Collections.singletonList(key), time);
        return result == null ? 0 : result;
    }

    public static void expireImmediately(String key) {
        redisTemplate.expire(key, 0L, TimeUnit.MILLISECONDS);
    }

    public static Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public static void set(String key, Object value, long expire) {
        redisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
    }

    public static Object hget(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    public static void hset(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    public static List<Object> hmget(String key, String... field) {
        return redisTemplate.opsForHash().multiGet(key, Arrays.asList(field));
    }

    public static void hmset(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    public static <T> T execute(RedisScript<T> script, List<Object> keys, Object... args) {
        return redisTemplate.execute(script, keys, args);
    }

    public static RedisTemplate<Object, Object> getRedisTemplate() {
        return redisTemplate;
    }

    public static class Locker implements Closeable {
        private final String key;
        private final String value;
        private final long expire;

        public Locker(String key) {
            this.key = "lock:" + key;
            this.value = String.valueOf(System.currentTimeMillis());
            this.expire = 300;
        }

        @Override
        public void close() {
            this.unlock();
        }

        public boolean lock() {
            String LOCK_LUA_SCRIPT = "return redis.call('set',KEYS[1],ARGV[1],'EX',ARGV[2],'NX')";
            RedisScript<Boolean> redisScript = new DefaultRedisScript<>(LOCK_LUA_SCRIPT, Boolean.class);
            Boolean result = RedisUtil.execute(redisScript, Collections.singletonList(key), value, expire);
            return Objects.equals(result, true);
        }

        public void unlock() {
            String UNLOCK_LUA_SCRIPT = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return false end";
            RedisScript<Boolean> redisScript = new DefaultRedisScript<>(UNLOCK_LUA_SCRIPT, Boolean.class);
            Boolean result = RedisUtil.execute(redisScript, Collections.singletonList(key), value);
            if (result == null || !result) {
                log.error("redis锁释放失败，key:{},value:{}", key, value);
            }
        }
    }
}

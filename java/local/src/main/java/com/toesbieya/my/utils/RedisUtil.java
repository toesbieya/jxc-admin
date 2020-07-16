package com.toesbieya.my.utils;

import com.google.common.io.ByteStreams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.*;
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
    private static RedisTemplate<String, Object> redisTemplate;
    private static StringRedisTemplate stringRedisTemplate;

    static {
        try {
            INCREMENT_AND_EXPIRE_SCRIPT = new String(ByteStreams.toByteArray(new ClassPathResource("/script/increase_and_expire.lua").getInputStream()));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    public RedisUtil(RedisTemplate<String, Object> redisTemplate, StringRedisTemplate stringRedisTemplate) {
        RedisUtil.redisTemplate = redisTemplate;
        RedisUtil.stringRedisTemplate = stringRedisTemplate;
    }

    public static Set<String> keys(String match) {
        return redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> result = new HashSet<>();

            Cursor<byte[]> cursor =
                    connection.scan(
                            new ScanOptions
                                    .ScanOptionsBuilder()
                                    .match(match)
                                    .count(2000)
                                    .build()
                    );

            while (cursor.hasNext()) {
                result.add(new String(cursor.next()));
            }

            return result;
        });
    }

    public static boolean exist(String key) {
        Boolean result = redisTemplate.hasKey(key);
        return result != null && result;
    }

    public static void del(String... keys) {
        redisTemplate.delete(Arrays.asList(keys));
    }

    public static long incrAndExpire(String key, int time) {
        RedisScript<Long> redisScript = new DefaultRedisScript<>(INCREMENT_AND_EXPIRE_SCRIPT, Long.class);
        Long result = redisTemplate.execute(redisScript, Collections.singletonList(key), time);
        return result == null ? 0 : result;
    }

    public static Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public static void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public static void set(String key, Object value, long expire) {
        redisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
    }

    public static Object hget(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    public static Map<Object, Object> hgetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public static void hset(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    //fields需要是String类型
    public static void hdel(String key, Object... fields) {
        redisTemplate.opsForHash().delete(key, fields);
    }

    public static List<Object> hmget(String key, String... field) {
        return redisTemplate.opsForHash().multiGet(key, Arrays.asList(field));
    }

    public static void hmset(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    public static long sadd(String key, Object... members) {
        Long result = redisTemplate.opsForSet().add(key, members);
        return result == null ? 0 : result;
    }

    public static void srem(String key, Object... members) {
        redisTemplate.opsForSet().remove(key, members);
    }

    public static long scard(String key) {
        Long result = redisTemplate.opsForSet().size(key);
        return result == null ? 0 : result;
    }

    public static Set<Object> smembers(String key) {
        Set<Object> result = redisTemplate.opsForSet().members(key);
        return result == null ? Collections.emptySet() : result;
    }

    public static RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    public static StringRedisTemplate getStringRedisTemplate() {
        return stringRedisTemplate;
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
            Boolean result = redisTemplate.execute(redisScript, Collections.singletonList(key), value, expire);
            return result != null;
        }

        public void unlock() {
            String UNLOCK_LUA_SCRIPT = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return false end";
            RedisScript<Boolean> redisScript = new DefaultRedisScript<>(UNLOCK_LUA_SCRIPT, Boolean.class);
            Boolean result = redisTemplate.execute(redisScript, Collections.singletonList(key), value);
            if (result == null || !result) {
                log.error("redis锁释放失败，key:{},value:{}", key, value);
            }
        }
    }
}

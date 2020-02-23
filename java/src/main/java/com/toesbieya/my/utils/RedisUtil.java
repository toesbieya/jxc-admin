package com.toesbieya.my.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.io.ByteStreams;
import com.toesbieya.my.constant.SessionConstant;
import com.toesbieya.my.model.entity.SysUser;
import com.toesbieya.my.module.redis.RedisLockType;
import com.toesbieya.my.module.redis.RedisModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedisUtil {
    private static String GET_DOCUMENT_ID_SCRIPT = null;
    private static String INCREMENT_AND_EXPIRE_SCRIPT = null;
    private static StringRedisTemplate stringRedisTemplate;
    private static RedisTemplate<Object, Object> redisTemplate;

    static {
        try {
            GET_DOCUMENT_ID_SCRIPT = new String(ByteStreams.toByteArray(new ClassPathResource("/script/get_document_id.lua").getInputStream()));
            INCREMENT_AND_EXPIRE_SCRIPT = new String(ByteStreams.toByteArray(new ClassPathResource("/script/increase_and_expire.lua").getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    public RedisUtil(StringRedisTemplate stringRedisTemplate, RedisTemplate<Object, Object> redisTemplate) {
        RedisUtil.stringRedisTemplate = stringRedisTemplate;
        RedisUtil.redisTemplate = redisTemplate;
    }

    public static boolean exist(String key) {
        Boolean result = stringRedisTemplate.hasKey(key);
        return result != null && result;
    }

    public static long incrAndExpire(String key, int time) {
        RedisScript<Long> redisScript = new DefaultRedisScript<>(INCREMENT_AND_EXPIRE_SCRIPT, Long.class);
        Long result = redisTemplate.execute(redisScript, Collections.singletonList(key), time);
        return result == null ? 0 : result;
    }

    public static void del(String key) {
        stringRedisTemplate.delete(key);
    }

    public static void expire(String key) {
        stringRedisTemplate.expire(key, 0L, TimeUnit.MILLISECONDS);
    }

    public static Set<String> keys(String key) {
        return stringRedisTemplate.keys(key + '*');
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

    public static String getDocumentID(String type) {
        if (!Arrays.asList(RedisModule.DOCUMENTS_TYPE).contains(type)) {
            log.error("单据类型有误，【{}】不存在", type);
            return null;
        }

        RedisScript<Long> redisScript = new DefaultRedisScript<>(GET_DOCUMENT_ID_SCRIPT, Long.class);
        Long result = stringRedisTemplate.execute(
                redisScript,
                Arrays.asList(RedisLockType.UPDATE_DOCUMENT_ID, RedisModule.DOCUMENTS_KEY),
                String.valueOf(DateUtil.getTimestampNow()),
                type
        );
        if (result == null || result <= 1) return null;
        return type + DateUtil.dateFormat(DateTimeFormatter.BASIC_ISO_DATE) + String.format("%04d", result - 1);
    }

    public static SysUser getUserByRedisKey(String key) {
        JSONObject o = (JSONObject) redisTemplate.opsForHash().get(key, "sessionAttr:" + SessionConstant.USER_KEY);
        if (o == null) {
            return null;
        }
        return JSONObject.toJavaObject(o, SysUser.class);
    }

    public static StringRedisTemplate getStringRedisTemplate() {
        return stringRedisTemplate;
    }
}

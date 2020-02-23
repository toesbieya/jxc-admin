package com.toesbieya.my.config;

import com.alibaba.fastjson.JSON;
import com.toesbieya.my.listener.RedisSessionListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableRedisHttpSession()
public class HttpSessionConfig {

    @Bean
    public RedisSessionListener httpSessionEventPublisher() {
        return new RedisSessionListener();
    }

    @Bean("springSessionDefaultRedisSerializer")
    public RedisSerializer<Object> defaultRedisSerializer() {
        return new JsonRedisSerializer<>(Object.class);
    }

    private class JsonRedisSerializer<T> implements RedisSerializer<T> {

        private final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

        private Class<T> clazz;

        public JsonRedisSerializer(Class<T> clazz) {
            this.clazz = clazz;
        }

        @Override
        public byte[] serialize(T t) throws SerializationException {
            if (t == null) {
                return new byte[0];
            }
            return JSON.toJSONString(t).getBytes(DEFAULT_CHARSET);
        }

        @Override
        public T deserialize(byte[] bytes) throws SerializationException {
            if (bytes == null || bytes.length <= 0) {
                return null;
            }
            String str = new String(bytes, DEFAULT_CHARSET);
            return JSON.parseObject(str, clazz);
        }
    }
}

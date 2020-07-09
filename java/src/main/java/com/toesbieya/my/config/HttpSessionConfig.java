package com.toesbieya.my.config;

import com.alibaba.fastjson.JSON;
import com.toesbieya.my.model.entity.SysUser;
import com.toesbieya.my.module.SocketModule;
import com.toesbieya.my.utils.Util;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 28800)
public class HttpSessionConfig {
    @Bean
    public HttpSessionListener httpSessionEventPublisher() {
        return new HttpSessionListener() {
            @Override
            public void sessionCreated(HttpSessionEvent se) {
            }

            @Override
            public void sessionDestroyed(HttpSessionEvent se) {
                SysUser user = Util.getUser(se.getSession());
                if (user != null) {
                    SocketModule.logout(user.getId(), "登陆状态过期，请重新登陆");
                }
            }
        };
    }

    @Bean("springSessionDefaultRedisSerializer")
    public RedisSerializer<Object> defaultRedisSerializer() {
        return new JsonRedisSerializer<>(Object.class);
    }

    private class JsonRedisSerializer<T> implements RedisSerializer<T> {

        private final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

        private final Class<T> clazz;

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

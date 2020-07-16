package cn.toesbieya.jxc.socket.config;

import cn.toesbieya.jxc.common.constant.SessionConstant;
import cn.toesbieya.jxc.common.constant.SocketConstant;
import cn.toesbieya.jxc.common.model.vo.SocketEventVo;
import cn.toesbieya.jxc.common.utils.RedisUtil;
import cn.toesbieya.jxc.common.utils.SessionUtil;
import cn.toesbieya.jxc.socket.server.WebSocketServer;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.List;
import java.util.Properties;

@Configuration
@Slf4j
public class RedisEventListenerConfig {
    private WebSocketServer server;

    @Autowired
    public void setSocketServer(WebSocketServer server) {
        this.server = server;
    }

    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory factory) {
        modifyRedisEventConfig(factory);

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);

        addEventSendListener(container);
        addExpireListener(container);

        return container;
    }

    //发送消息的监听器
    private void addEventSendListener(RedisMessageListenerContainer container) {
        container.addMessageListener(
                (message, bytes) -> {
                    SocketEventVo vo = JSON.parseObject(message.toString(), SocketEventVo.class);

                    if (vo == null) return;

                    int type = vo.getType();
                    String eventName = vo.getEvent();
                    Object data = vo.getData();
                    List<Integer> to = vo.getTo();

                    //登出消息
                    if (type == SocketConstant.REDIS_EVENT_LOGOUT) {
                        if (to != null) {
                            to.forEach(id -> server.logout(id, (String) data));
                        }
                    }
                    //指定用户
                    else if (type == SocketConstant.REDIS_EVENT_SPECIFIC) {
                        if (to != null) {
                            to.forEach(id -> server.sendEvent(eventName, id, data));
                        }
                    }
                    //广播消息
                    else if (type == SocketConstant.REDIS_EVENT_BROADCAST) {
                        server.broadcast(eventName, data);
                    }
                },
                new ChannelTopic(SocketConstant.REDIS_EVENT_TOPIC_SEND)
        );
    }

    //key过期的监听器
    private void addExpireListener(RedisMessageListenerContainer container) {
        //监听key过期事件
        String expireTopic = SessionConstant.REDIS_EXPIRE_TOPIC_PREFIX + SessionConstant.REDIS_NAMESPACE;

        container.addMessageListener(
                (message, bytes) -> {
                    String event = message.toString();
                    String channel = new String(message.getChannel());

                    if (!"del".equals(event) && !"expired".equals(event)
                            || !channel.startsWith(expireTopic)) {
                        return;
                    }

                    String token = channel.replace(expireTopic, "");
                    Integer uid = SessionUtil.getUidFromToken(token);

                    server.logout(uid, "登陆信息过期，请重新登陆");

                    //删除离线表信息
                    RedisUtil.hdel(SocketConstant.REDIS_OFFLINE_USER, SessionConstant.REDIS_NAMESPACE + token);
                },
                new PatternTopic(expireTopic + "*")
        );
    }

    //将redis的notify-keyspace-events配置改为Kgx
    private void modifyRedisEventConfig(RedisConnectionFactory factory) {
        RedisConnection connection = factory.getConnection();
        try {
            Properties config = connection.getConfig("notify-keyspace-events");

            String notifyOptions =
                    config == null || config.isEmpty()
                            ? ""
                            : config.getProperty(config.stringPropertyNames().iterator().next());

            String customizedNotifyOptions = notifyOptions;

            if (!customizedNotifyOptions.contains("K")) {
                customizedNotifyOptions += "K";
            }

            boolean A = customizedNotifyOptions.contains("A");

            if (!(A || customizedNotifyOptions.contains("g"))) {
                customizedNotifyOptions += "g";
            }
            if (!(A || customizedNotifyOptions.contains("x"))) {
                customizedNotifyOptions += "x";
            }
            if (!notifyOptions.equals(customizedNotifyOptions)) {
                connection.setConfig("notify-keyspace-events", customizedNotifyOptions);
            }
        }
        finally {
            try {
                connection.close();
            }
            catch (Exception ex) {
                log.error("Error closing RedisConnection", ex);
            }
        }
    }
}

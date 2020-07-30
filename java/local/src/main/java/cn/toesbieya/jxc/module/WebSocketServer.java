package cn.toesbieya.jxc.module;

import cn.toesbieya.jxc.constant.SessionConstant;
import cn.toesbieya.jxc.constant.SocketConstant;
import cn.toesbieya.jxc.model.vo.SocketOfflineVo;
import cn.toesbieya.jxc.utils.SessionUtil;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import cn.toesbieya.jxc.utils.RedisUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

@Component
@Slf4j
public class WebSocketServer {
    private final ConcurrentHashMap<Integer, UserObject> socketMap = new ConcurrentHashMap<>(128);
    private final SocketIOServer server;

    @Autowired
    public WebSocketServer(SocketIOServer server) {
        this.server = server;

        //启动socket服务前清空redis中的在线用户信息
        RedisUtil.del(SocketConstant.REDIS_ONLINE_USER);

        server.start();
    }

    public void sendEvent(String event, Integer uid, Object... data) {
        sendEvent(event, uid, data, null);
    }

    public void sendEvent(String event, Integer uid, Object data, BiConsumer<UserObject, SocketIOClient> consumer) {
        UserObject obj = socketMap.get(uid);
        if (obj != null) {
            sendEvent(event, obj, data, consumer);
        }
    }

    public void sendEvent(String event, UserObject obj, Object data, BiConsumer<UserObject, SocketIOClient> consumer) {
        SocketIOClient client = server.getClient(obj.getUuid());

        if (client == null) return;

        client.sendEvent(event, data);

        if (consumer != null) consumer.accept(obj, client);
    }

    public void broadcast(String event, Object... data) {
        server.getBroadcastOperations().sendEvent(event, data);
    }

    public void logout(Integer uid, String msg) {
        logout(uid, null, msg);
    }

    public void logout(Integer uid, String token, String msg) {
        UserObject obj = socketMap.get(uid);

        //如果传入token且token不同说明不是当前登陆的用户，直接返回
        if (obj == null || !StringUtils.isEmpty(token) && !obj.getToken().equals(token)) return;

        sendEvent(SocketConstant.EVENT_LOGOUT, obj, msg, (o, client) -> {
            SessionUtil.remove(o.getToken());
            client.disconnect();
        });
    }

    @OnConnect
    public void onConnect(SocketIOClient client) {
        UserObject obj = new UserObject(client);
        String sessionKey = obj.getKey();
        Integer uid = obj.getUid();

        //用户session是否过期？
        if (!RedisUtil.exist(sessionKey)) {
            client.sendEvent(SocketConstant.EVENT_LOGOUT, "登陆信息过期，请重新登陆");
            client.disconnect();
            return;
        }

        //如果存在，说明该账号重复登陆，强制登出
        if (socketMap.containsKey(uid)) {
            logout(uid, "该账号在其他位置登录");
        }

        socketMap.put(uid, obj);

        //放入在线用户ID集合
        RedisUtil.sadd(SocketConstant.REDIS_ONLINE_USER, uid);
        //移除离线表的信息
        RedisUtil.hdel(SocketConstant.REDIS_OFFLINE_USER, sessionKey);
    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        long now = System.currentTimeMillis();
        UserObject obj = new UserObject(client);

        Integer uid = obj.getUid();
        String sessionKey = obj.getKey();

        //重复登陆时，不需要执行操作
        UserObject already = socketMap.get(uid);
        if (already == null || !sessionKey.equals(already.getKey())) {
            return;
        }

        socketMap.remove(uid);

        //移除在线用户ID集合对应的ID
        RedisUtil.srem(SocketConstant.REDIS_ONLINE_USER, uid);

        //在用户session未过期时设置离线表信息
        if (RedisUtil.exist(sessionKey)) {
            RedisUtil.hset(
                    SocketConstant.REDIS_OFFLINE_USER,
                    sessionKey,
                    new SocketOfflineVo(uid, now)
            );
        }
    }

    @OnEvent("text")
    public void onEvent(SocketIOClient client, AckRequest ackRequest, String data) {
        client.sendEvent("response", "后台得到了数据");
    }

    @Data
    private static class UserObject {
        private String key;   //redis的键名
        private Integer uid;  //用户id
        private String token; //redis中的token
        private UUID uuid;    //socketClient自己的sessionID

        public UserObject(SocketIOClient client) {
            Map<String, List<String>> params = client.getHandshakeData().getUrlParams();
            List<String> list = params.get("id");
            this.uid = Integer.valueOf(list.get(0));
            list = params.get("token");
            this.token = list.get(0);
            this.uuid = client.getSessionId();
            this.key = SessionConstant.REDIS_NAMESPACE + this.token;
        }
    }
}

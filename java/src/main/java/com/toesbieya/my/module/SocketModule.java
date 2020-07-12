package com.toesbieya.my.module;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.toesbieya.my.constant.SocketConstant;
import com.toesbieya.my.utils.SessionUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

@Component
@Slf4j
public class SocketModule {
    private static final ConcurrentHashMap<Integer, UserObject> socketMap = new ConcurrentHashMap<>(128);
    private static SocketIOServer server;

    @Autowired
    public SocketModule(SocketIOServer server) {
        SocketModule.server = server;
        server.start();
    }

    public static void sendEvent(String event, Integer uid, Object... data) {
        sendEvent(event, uid, data, null);
    }

    public static void sendEvent(String event, Integer uid, Object data, BiConsumer<UserObject, SocketIOClient> consumer) {
        UserObject obj = socketMap.get(uid);

        if (obj == null) return;

        SocketIOClient client = server.getClient(obj.getUuid());

        if (client == null) return;

        client.sendEvent(event, data);

        if (consumer != null) consumer.accept(obj, client);
    }

    public static void broadcast(String event, Object... data) {
        server.getBroadcastOperations().sendEvent(event, data);
    }

    public static boolean online(Integer uid) {
        return uid != null && socketMap.containsKey(uid);
    }

    public static void logout(Integer uid, String msg) {
        sendEvent(SocketConstant.EVENT_LOGOUT, uid, msg, (obj, client) -> {
            SessionUtil.remove(obj.getToken());
            client.disconnect();
            socketMap.remove(uid);
        });
    }

    public static int getOnlineNum() {
        return socketMap.size();
    }

    @OnConnect
    public void onConnect(SocketIOClient client) {
        UserObject obj = new UserObject(client);
        Integer uid = obj.getUid();

        if (socketMap.containsKey(uid)) {
            logout(uid, "该账号在其他位置登录");
        }
        else socketMap.put(uid, obj);
    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        UserObject obj = new UserObject(client);
        Integer uid = obj.getUid();
        UserObject already = socketMap.get(uid);

        if (already == null) return;

        //只有token相同时才移除
        if (obj.getToken().equals(already.getToken())) {
            socketMap.remove(uid);
        }
    }

    @OnEvent("text")
    public void onEvent(SocketIOClient client, AckRequest ackRequest, String data) {
        client.sendEvent("response", "后台得到了数据");
    }

    @Data
    private static class UserObject {
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
        }
    }
}

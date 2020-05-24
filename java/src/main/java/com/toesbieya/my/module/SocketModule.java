package com.toesbieya.my.module;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.toesbieya.my.constant.SessionConstant;
import com.toesbieya.my.constant.SocketConstant;
import com.toesbieya.my.utils.RedisUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class SocketModule {
    private static final ConcurrentHashMap<Integer, UserObject> socketMap = new ConcurrentHashMap<>(SessionConstant.PREDICT_MAX_USER);
    private static SocketIOServer server;

    @Autowired
    public SocketModule(SocketIOServer server) {
        SocketModule.server = server;
        server.start();
    }

    public static void logout(Integer uid, String msg) {
        UserObject obj = socketMap.get(uid);
        if (obj == null) return;
        SocketIOClient client = server.getClient(obj.getUuid());
        if (client == null) return;
        client.sendEvent(SocketConstant.EVENT_LOGOUT, msg);
        RedisUtil.expire(SessionConstant.REDIS_NAMESPACE + obj.getSessionID());
        client.disconnect();
        socketMap.remove(uid);
    }

    public static boolean online(Integer uid) {
        return uid != null && socketMap.containsKey(uid);
    }

    @OnConnect
    public void onConnect(SocketIOClient client) {
        UserObject obj = new UserObject(client);
        Integer uid = obj.getUid();
        if (socketMap.containsKey(uid)) logout(uid, "该账号在其他位置登录");
        else socketMap.put(uid, obj);
    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        UserObject obj = new UserObject(client);
        Integer uid = obj.getUid();
        UserObject already = socketMap.get(uid);
        if (already == null) return;
        //只有sessionID相同时才移除
        if (obj.getSessionID().equals(already.getSessionID())) {
            socketMap.remove(uid);
        }
    }

    @OnEvent("text")
    public void onEvent(SocketIOClient client, AckRequest ackRequest, String data) {
        System.out.println(getIp(client) + "：客户端：************" + data);
        client.sendEvent("response", "后台得到了数据");
    }

    public static int getOnlineNum() {
        return socketMap.size();
    }

    private String getIp(SocketIOClient client) {
        String sa = client.getRemoteAddress().toString();
        return sa.substring(1, sa.indexOf(":"));
    }

    @Data
    private static class UserObject {
        private Integer uid;//用户id
        private String sessionID;//redis中的sessionID
        private UUID uuid;//socketClient自己的sessionID

        public UserObject(SocketIOClient client) {
            Map<String, List<String>> params = client.getHandshakeData().getUrlParams();
            List<String> list = params.get("id");
            this.uid = Integer.valueOf(list.get(0));
            list = params.get("session_id");
            this.sessionID = list.get(0);
            this.uuid = client.getSessionId();
        }
    }
}

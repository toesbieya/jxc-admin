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

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class SocketModule {
    private static ConcurrentHashMap<Integer, UserClient> socketMap = new ConcurrentHashMap<>(SessionConstant.PREDICT_MAX_USER);
    private final SocketIOServer server;

    @Autowired
    public SocketModule(SocketIOServer server) {
        this.server = server;
    }

    public static void logout(Integer uid, String msg) {
        UserClient client = socketMap.get(uid);
        if (client == null) return;
        client.getClient().sendEvent(SocketConstant.EVENT_LOGOUT, msg);
        RedisUtil.expire(SessionConstant.REDIS_NAMESPACE + client.getSessionID());
        client.getClient().disconnect();
        client = null;
        socketMap.remove(uid);
    }

    public static boolean online(Integer uid) {
        return uid != null && socketMap.containsKey(uid);
    }

    @PostConstruct
    public void init() {
        server.start();
    }

    @OnConnect
    public void onConnect(SocketIOClient client) {
        UserClient userClient = new UserClient(client);
        int uid = userClient.getUid();
        if (socketMap.get(uid) != null) logout(uid, "该账号在其他位置登录");
        socketMap.put(uid, userClient);
    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        UserClient userClient = new UserClient(client);
        UserClient already = socketMap.get(userClient.getUid());
        if (already == null) return;
        //只有sessionID相同时才移除
        if (userClient.getSessionID().equals(already.getSessionID())) {
            socketMap.remove(userClient.getUid());
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

    private int getUidFromIOClient(SocketIOClient client) {
        Map<String, List<String>> params = client.getHandshakeData().getUrlParams();
        List<String> list = params.get("id");
        if (list != null && list.size() > 0) {
            return Integer.parseInt(list.get(0));
        }
        return -1;
    }

    @Data
    private static class UserClient {
        private Integer uid;
        private String sessionID;
        private SocketIOClient client;

        public UserClient(SocketIOClient client) {
            Map<String, List<String>> params = client.getHandshakeData().getUrlParams();
            List<String> list = params.get("id");
            if (list != null && list.size() > 0) {
                this.uid = Integer.parseInt(list.get(0));
            }
            list = params.get("session_id");
            if (list != null && list.size() > 0) {
                this.sessionID = list.get(0);
            }
            this.client = client;
        }
    }
}

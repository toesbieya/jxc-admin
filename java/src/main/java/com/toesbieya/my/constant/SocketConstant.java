package com.toesbieya.my.constant;

import com.toesbieya.my.utils.YmlUtil;

public class SocketConstant {
    public static final String HOSTNAME = (String) YmlUtil.get("socket.hostname");
    public static final int PORT = (int) YmlUtil.get("socket.port");
    public static final int MAX_FRAME_PAYLOAD = (int) YmlUtil.get("socket.max-frame-payload");
    public static final int MAX_HTTP_CONTENT = (int) YmlUtil.get("socket.max-http-content");
    public static final String EVENT_LOGOUT = (String) YmlUtil.get("socket.event.logout");
}

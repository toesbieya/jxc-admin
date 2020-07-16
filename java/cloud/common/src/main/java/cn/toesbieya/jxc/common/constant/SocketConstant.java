package cn.toesbieya.jxc.common.constant;

public class SocketConstant {
    //web-socket服务端订阅的redis主题名称
    public static String REDIS_EVENT_TOPIC_SEND = "socket-event-send";

    //指定用户
    public static int REDIS_EVENT_SPECIFIC = 0;
    //广播
    public static int REDIS_EVENT_BROADCAST = 1;
    //登出
    public static int REDIS_EVENT_LOGOUT = 2;

    //存放于redis中的在线用户的ID集合的键
    public static String REDIS_ONLINE_USER = "socket:online-user";
    //存放于redis中的离线用户的离线时间的键
    public static String REDIS_OFFLINE_USER = "socket:offline-user";

    //登出事件
    public static final String EVENT_LOGOUT = "logout";
    //新消息事件
    public static final String EVENT_NEW_MESSAGE = "new-message";
}

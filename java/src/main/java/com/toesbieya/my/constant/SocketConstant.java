package com.toesbieya.my.constant;

public class SocketConstant {
    public static String REDIS_EVENT_TOPIC_SEND = "socket:event:send";
    public static int REDIS_EVENT_SPECIFIC = 0;
    public static int REDIS_EVENT_BROADCAST = 1;
    public static int REDIS_EVENT_LOGOUT = 2;

    public static String REDIS_ONLINE_NUM = "socket:online-num";
    public static String REDIS_ONLINE_USER = "socket:online-user";

    public static final String EVENT_LOGOUT = "logout";
    public static final String EVENT_NEW_MESSAGE = "new-message";
}

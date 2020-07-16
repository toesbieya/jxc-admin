package cn.toesbieya.jxc.common.constant;

public class SessionConstant {
    //session过期时间，单位秒
    public static long EXPIRE = 3600 * 8;

    //存储用户信息的键的前缀
    public static String REDIS_NAMESPACE = "sessions:";

    //key过期事件的前缀
    public static String REDIS_EXPIRE_TOPIC_PREFIX = "__keyspace@0__:";

    //http headers中token的字段名
    public static String TOKEN_KEY = "X-Token";

    //区别无活动用户的时间间隔，单位秒
    public static long NO_ACTION_INTERVAL = 1800;
}

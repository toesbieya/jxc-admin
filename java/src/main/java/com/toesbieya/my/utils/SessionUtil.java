package com.toesbieya.my.utils;

import com.alibaba.fastjson.JSONObject;
import com.toesbieya.my.constant.SessionConstant;
import com.toesbieya.my.model.vo.UserVo;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class SessionUtil {
    private static long expire = 3600 * 8;

    public static void remove(String token) {
        if (StringUtils.isEmpty(token)) return;

        RedisUtil.expireImmediately(SessionConstant.REDIS_NAMESPACE + token);
    }

    public static void save(UserVo user) {
        String token = user.getToken();

        if (StringUtils.isEmpty(token)) return;

        RedisUtil.set(SessionConstant.REDIS_NAMESPACE + token, user, expire);
    }

    public static UserVo get() {
        UserVo user = ThreadUtil.getUser();
        return user == null ? get(WebUtil.getRequest()) : user;
    }

    public static UserVo get(String token) {
        if (StringUtils.isEmpty(token)) return null;

        JSONObject o = (JSONObject) RedisUtil.get(SessionConstant.REDIS_NAMESPACE + token);

        if (o == null) return null;

        return JSONObject.toJavaObject(o, UserVo.class);
    }

    public static UserVo get(HttpServletRequest request) {
        String token = request.getHeader(SessionConstant.TOKEN_KEY);
        return get(token);
    }
}

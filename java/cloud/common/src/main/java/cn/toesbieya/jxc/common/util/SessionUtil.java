package cn.toesbieya.jxc.common.util;

import cn.toesbieya.jxc.common.model.entity.SysUser;
import cn.toesbieya.jxc.common.model.vo.UserVo;
import com.alibaba.fastjson.JSONObject;
import cn.toesbieya.jxc.common.constant.SessionConstant;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class SessionUtil {
    //token中的连接符
    private static final String tokenSeparator = "-*-";

    public static void remove(String token) {
        if (StringUtils.isEmpty(token)) return;

        RedisUtil.del(SessionConstant.REDIS_NAMESPACE + token);
    }

    public static void save(UserVo user) {
        String token = user.getToken();

        if (StringUtils.isEmpty(token)) return;

        RedisUtil.set(SessionConstant.REDIS_NAMESPACE + token, user, SessionConstant.EXPIRE);
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

    public static String generateToken(SysUser user) {
        return user.getId() + tokenSeparator + Util.UUID();
    }

    public static Integer getUidFromToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }

        String[] arr = token.split(tokenSeparator);

        return arr.length > 1 ? Integer.valueOf(arr[0]) : null;
    }
}

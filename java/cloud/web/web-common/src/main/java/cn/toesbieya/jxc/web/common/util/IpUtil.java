package cn.toesbieya.jxc.web.common.util;

import cn.toesbieya.jxc.common.util.RedisUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class IpUtil {
    private static final List<Function<String, String>> handlerList = new ArrayList<>();

    static {
        handlerList.add(new PconlineApi());
        handlerList.add(new IphelpApi());
        handlerList.add(new IpipApi());
    }

    private static final String REDIS_IP_CACHE_KEY = "ipCache";

    public static String getIp(HttpServletRequest request) {
        String ip = "";
        if (request != null) {
            ip = request.getHeader("x-forwarded-for");
            if (invalidIp(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (invalidIp(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (invalidIp(ip)) {
                ip = request.getRemoteAddr();
            }
        }
        return ip;
    }

    public static String getIpAddress(String ip) {
        if (StringUtils.isEmpty(ip) || ip.equals("127.0.0.1")) {
            return null;
        }
        String cache = (String) RedisUtil.hget(REDIS_IP_CACHE_KEY, ip);
        if (!StringUtils.isEmpty(cache)) {
            return cache;
        }
        for (Function<String, String> handler : handlerList) {
            try {
                cache = handler.apply(ip);
            }
            catch (Exception e) {
                continue;
            }
            if (!StringUtils.isEmpty(cache)) {
                RedisUtil.hset(REDIS_IP_CACHE_KEY, ip, cache);
                return cache;
            }
        }
        return null;
    }

    private static boolean invalidIp(String ip) {
        return StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip);
    }

    private static class IphelpApi implements Function<String, String> {
        @Override
        public String apply(String ip) {
            String url = "https://ip.help.bj.cn/?ip=" + ip;
            String response = HttpUtil.get(url);
            JSONObject result = JSON.parseObject(response);
            if (!result.getString("status").equals("200") || result.get("data") == null) {
                return null;
            }
            JSONArray array = result.getJSONArray("data");
            if (CollectionUtils.isEmpty(array)) return null;
            result = array.getJSONObject(0);
            return handleAddress(result.getString("province"), result.getString("city"));
        }
    }

    //https://www.ipip.net/support/api.html 每天限1000次
    private static class IpipApi implements Function<String, String> {
        @Override
        public String apply(String ip) {
            String url = "https://freeapi.ipip.net/" + ip;
            String response = HttpUtil.get(url);
            JSONArray result = JSON.parseArray(response);
            if (result == null || result.size() != 5) return null;
            return handleAddress(result.getString(1), result.getString(2));
        }
    }

    //这个不错
    private static class PconlineApi implements Function<String, String> {
        @Override
        public String apply(String ip) {
            String url = "http://whois.pconline.com.cn/ipJson.jsp?json=true&ip=" + ip;
            String response = HttpUtil.get(url);
            JSONObject result = JSON.parseObject(response);
            return handleAddress(result.getString("pro"), result.getString("city"));
        }
    }

    private static String handleAddress(String province, String city) {
        if (StringUtils.isEmpty(province) || StringUtils.isEmpty(city)) {
            return null;
        }
        else if (province.equals(city)) {
            return province;
        }
        return province + city;
    }
}

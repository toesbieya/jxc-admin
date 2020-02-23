package com.toesbieya.my.utils;

import com.toesbieya.my.model.vo.IpResultVo;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class IpUtil {
    private static final String API = "http://ip.taobao.com/service/getIpInfo.php";

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

    public static String getIpAddr(String ip) {
        if (StringUtils.isEmpty(ip) || ip.equals("127.0.0.1")) {
            return null;
        }
        String redisCache = (String) RedisUtil.hget(REDIS_IP_CACHE_KEY, ip);
        if (!StringUtils.isEmpty(redisCache)) {
            return redisCache;
        }
        IpResultVo vo = HttpUtil.get(API + "?ip=" + ip, IpResultVo.class);
        if (vo == null || vo.getCode() != 0 || vo.getData() == null) {
            return null;
        }
        String region = vo.getData().getString("region");
        String city = vo.getData().getString("city");
        if (StringUtils.isEmpty(region) || StringUtils.isEmpty(city)) {
            redisCache = null;
        }
        else if (region.equals(city)) {
            redisCache = region;
        }
        else {
            redisCache = region + city;
        }
        RedisUtil.hset(REDIS_IP_CACHE_KEY, ip, redisCache);
        return redisCache;
    }

    private static boolean invalidIp(String ip) {
        return ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip);
    }
}

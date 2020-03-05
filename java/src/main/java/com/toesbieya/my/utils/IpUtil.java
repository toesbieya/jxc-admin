package com.toesbieya.my.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class IpUtil {
    private static final RestTemplate restTemplate = new RestTemplate();

    private static List<IpAddressRequestHandler> handlerList = new ArrayList<>();

    static {
        handlerList.add(new PconlineApi());
        handlerList.add(new IphelpApi());
        handlerList.add(new IpipApi());
        handlerList.add(new TaobaoApi());
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
        if (handlerList.size() == 0) return null;
        for (IpAddressRequestHandler handler : handlerList) {
            try {
                cache = handler.getIpAddress(ip);
            } catch (Exception e) {
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
        return ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip);
    }

    private interface IpAddressRequestHandler {
        String getIpAddress(String ip);
    }

    //淘宝 部分ip无法查询
    private static class TaobaoApi implements IpAddressRequestHandler {
        @Override
        public String getIpAddress(String ip) {
            String url = "http://ip.taobao.com/service/getIpInfo.php?ip=" + ip;
            String response = restTemplate.getForObject(url, String.class);
            JSONObject result = JSON.parseObject(response);
            if (!result.getInteger("code").equals(0) || result.get("data") == null) {
                return null;
            }
            result = result.getJSONObject("data");
            return handleAddress(result.getString("region"), result.getString("city"));
        }
    }

    private static class IphelpApi implements IpAddressRequestHandler {
        @Override
        public String getIpAddress(String ip) {
            String url = "https://ip.help.bj.cn/?ip=" + ip;
            String response = restTemplate.getForObject(url, String.class);
            JSONObject result = JSON.parseObject(response);
            if (!result.getString("status").equals("200") || result.get("data") == null) {
                return null;
            }
            JSONArray array = result.getJSONArray("data");
            if (array.size() == 0) return null;
            result = array.getJSONObject(0);
            return handleAddress(result.getString("province"), result.getString("city"));
        }
    }

    //https://www.ipip.net/support/api.html 每天限1000次
    private static class IpipApi implements IpAddressRequestHandler {
        @Override
        public String getIpAddress(String ip) {
            String url = "https://freeapi.ipip.net/" + ip;
            String response = restTemplate.getForObject(url, String.class);
            JSONArray result = JSON.parseArray(response);
            if (result.size() != 5) return null;
            return handleAddress(result.getString(1), result.getString(2));
        }
    }

    //这个不错
    private static class PconlineApi implements IpAddressRequestHandler {
        @Override
        public String getIpAddress(String ip) {
            String url = "http://whois.pconline.com.cn/ipJson.jsp?json=true&ip=" + ip;
            String response = restTemplate.getForObject(url, String.class);
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

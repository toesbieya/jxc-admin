package com.toesbieya.my.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
public class HttpUtil {
    private static final RestTemplate restTemplate = new RestTemplate();

    public static String get(String url) {
        return httpGet(url, String.class);
    }

    public static <T> T get(String url, Class<T> t) {
        return httpGet(url, t);
    }

    public static <T> T get(String url, Class<T> t, Map<String, Object> map) {
        return httpGet(url + toUrlParams(map), t);
    }

    @SuppressWarnings("unchecked")
    private static <T> T httpGet(String url, Class<T> t) {
        String response;
        try {
            response = restTemplate.getForObject(url, String.class);
        } catch (RestClientException e) {
            log.error("http请求{}出错：{}", url, e);
            return null;
        }
        if (StringUtils.isEmpty(response)) {
            return null;
        }
        if (t == String.class) {
            return (T) response;
        }
        return JSON.parseObject(response, t);
    }

    private static String toUrlParams(Map<String, Object> source) {
        if (source.size() <= 0) {
            return "";
        }
        return "?" + Joiner.on("&").useForNull("").withKeyValueSeparator("=").join(source);
    }
}

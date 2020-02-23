package com.toesbieya.my.module.request;

import com.toesbieya.my.model.entity.SysResource;
import com.toesbieya.my.service.SysResourceService;
import com.toesbieya.my.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Component
@Slf4j
public class RequestModule {
    private final static String IP_LIMIT_KEY = "limit:url-ip:";
    private final static String BLACK_IP_KEY = "limit:black-ip:";
    private final static int REQUEST_URL_INIT_VALUE = 128;

    //判断能拿到令牌的最大时间，毫秒
    private final static long TIME_OUT = 200L;

    //所有controller的<url,mappingMethod>，一个url只会有一个mappingMethod
    private final static HashMap<String, String> requestUrlMap = new HashMap<>(REQUEST_URL_INIT_VALUE);

    //ip暂时拉黑的时间，单位秒
    private final static long EXPIRE_SECONDS = 10L;

    //url的总访问频率限制
    private static ConcurrentHashMap<String, RateLimiterHelper> limitMap;

    private RequestMappingHandlerMapping mapping;
    private SysResourceService resourceService;

    //根据请求地址和请求方法判断是否为合法请求
    public static boolean urlExistMapping(String url, String method) {
        String s = requestUrlMap.get(url);
        return s != null && s.equals(method);
    }

    /**
     * 未在resource表中配置的不进行限制
     *
     * @param url 请求的url
     * @param ip  访问IP
     * @return 0：ip被暂时拉黑，-1：该url的服务暂时不可用，-2：该ip访问该url时超出规定速度，1：通过
     */
    public static int pass(String url, String ip) {
        String redisKey = url + "-" + ip;

        //判断ip是否在黑名单中
        if (RedisUtil.exist(BLACK_IP_KEY + redisKey)) return 0;

        //判断是否超出url的总访问频率
        RateLimiterHelper helper = limitMap.get(url);
        if (helper == null) return 1;
        if (!helper.getLimiter().tryAcquire(TIME_OUT, MILLISECONDS)) return -1;

        //判断此ip是否超出url的访问频率
        long count = RedisUtil.incrAndExpire(IP_LIMIT_KEY + redisKey, 1);
        if (helper.getIpRate() > count - 1) return 1;

        //ip暂时拉黑
        RedisUtil.set(BLACK_IP_KEY + redisKey, 1, EXPIRE_SECONDS);
        return -2;
    }

    @PostConstruct
    public void init() {
        Instant start = Instant.now();

        initRequestMap();
        initLimitMap();

        Instant end = Instant.now();
        log.info("路由模块启动成功，耗时：{}毫秒", ChronoUnit.MILLIS.between(start, end));
    }

    private void initRequestMap() {
        // 获取url与类和方法的对应信息
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();

        for (RequestMappingInfo info : map.keySet()) {
            Set<String> patterns = info.getPatternsCondition().getPatterns();
            Set<RequestMethod> methods = info.getMethodsCondition().getMethods();
            if (methods.size() <= 0) {
                log.warn("url{}没有设置请求方法", patterns);
                continue;
            }
            requestUrlMap.put(patterns.iterator().next(), methods.iterator().next().toString());
        }
    }

    private void initLimitMap() {
        limitMap = new ConcurrentHashMap<>();
        List<SysResource> resources = resourceService.getAll();
        for (SysResource resource : resources) {
            RateLimiterHelper helper = new RateLimiterHelper(resource.getTotal_rate(), resource.getIp_rate());
            limitMap.put(resource.getUrl(), helper);
        }
    }

    public static void updateRate(String url,int totalRate,int ipRate) {
        limitMap.remove(url);
        limitMap.put(url, new RateLimiterHelper(totalRate, ipRate));
    }

    @Autowired
    public void setMapping(RequestMappingHandlerMapping mapping) {
        this.mapping = mapping;
    }

    @Autowired
    public void setResourceService(SysResourceService resourceService) {
        this.resourceService = resourceService;
    }
}

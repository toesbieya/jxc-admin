package cn.toesbieya.jxc.module.request;

import cn.toesbieya.jxc.model.entity.SysResource;
import cn.toesbieya.jxc.service.SysResourceService;
import cn.toesbieya.jxc.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Component
@Slf4j
public class RequestModule {
    private final static String IP_LIMIT_KEY = "limit:url-ip:";
    private final static String BLACK_IP_KEY = "limit:black-ip:";
    //判断能拿到令牌的最大时间，毫秒
    private final static long TIME_OUT = 200L;
    //ip暂时拉黑的时间，单位秒
    private final static long EXPIRE_SECONDS = 10L;
    //url的总访问频率限制
    private static ConcurrentHashMap<String, RateLimiterHelper> limitMap;
    private SysResourceService resourceService;

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

        initLimitMap();

        Instant end = Instant.now();
        log.info("路由模块启动成功，耗时：{}毫秒", ChronoUnit.MILLIS.between(start, end));
    }

    private void initLimitMap() {
        limitMap = new ConcurrentHashMap<>();
        List<SysResource> resources = resourceService.getAll();
        for (SysResource resource : resources) {
            RateLimiterHelper helper = new RateLimiterHelper(resource.getTotalRate(), resource.getIpRate());
            limitMap.put(resource.getUrl(), helper);
        }
    }

    public static void updateRate(String url, long totalRate, long ipRate) {
        limitMap.remove(url);
        limitMap.put(url, new RateLimiterHelper(totalRate, ipRate));
    }

    @Autowired
    public void setResourceService(SysResourceService resourceService) {
        this.resourceService = resourceService;
    }
}

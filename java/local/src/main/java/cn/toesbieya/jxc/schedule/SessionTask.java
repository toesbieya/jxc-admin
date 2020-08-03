package cn.toesbieya.jxc.schedule;

import cn.toesbieya.jxc.constant.SessionConstant;
import cn.toesbieya.jxc.constant.SocketConstant;
import cn.toesbieya.jxc.model.vo.SocketOfflineVo;
import com.alibaba.fastjson.JSONObject;
import cn.toesbieya.jxc.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
@DependsOn("redisUtil")
public class SessionTask {
    @PostConstruct
    public void run() {
        cleanSession();
    }

    @Async("scheduledExecutor")
    @Scheduled(cron = "0 */1 * * * ?")
    public void autoCleanSession() {
        cleanSession();
    }

    //清理session中未活动的用户
    private void cleanSession() {
        try {
            long now = System.currentTimeMillis();

            //获取所有用户session
            Set<String> sessionKeys = RedisUtil.keys(SessionConstant.REDIS_NAMESPACE + "*");

            //获取离线情况 (k:sessionKey,v:SocketOfflineVo)
            Map<Object, Object> offlineMap = RedisUtil.hgetAll(SocketConstant.REDIS_OFFLINE_USER);

            //移除无活动时间超出限定的用户
            sessionKeys.forEach(sessionKey -> {
                Object obj = offlineMap.get(sessionKey);
                if (obj != null) {
                    SocketOfflineVo offlineVo = ((JSONObject) obj).toJavaObject(SocketOfflineVo.class);
                    if (now > offlineVo.getTime() + SessionConstant.NO_ACTION_INTERVAL) {
                        //删除session
                        RedisUtil.del(sessionKey);

                        //删除离线表信息
                        RedisUtil.hdel(SocketConstant.REDIS_OFFLINE_USER, sessionKey);
                    }
                }
            });
        }
        catch (Exception e) {
            log.error("清理session失败", e);
        }
    }
}

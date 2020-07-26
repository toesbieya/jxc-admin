package cn.toesbieya.jxc.schedule.task;

import cn.toesbieya.jxc.common.constant.SessionConstant;
import cn.toesbieya.jxc.common.constant.SocketConstant;
import cn.toesbieya.jxc.common.model.vo.SocketOfflineVo;
import cn.toesbieya.jxc.common.utils.RedisUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
@Slf4j
@DependsOn("redisUtil")
public class SessionTask implements CommandLineRunner {
    @Override
    public void run(String... args) {
        cleanSession();
    }

    @Async("scheduledExecutor")
    @Scheduled(cron = "0 */10 * * * ?")
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

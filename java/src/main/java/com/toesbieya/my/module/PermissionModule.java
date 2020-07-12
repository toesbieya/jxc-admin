package com.toesbieya.my.module;

import com.toesbieya.my.model.entity.SysResource;
import com.toesbieya.my.model.vo.UserVo;
import com.toesbieya.my.service.SysResourceService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class PermissionModule {
    private final static ConcurrentHashMap<String, Integer> urlMap = new ConcurrentHashMap<>(128);
    private final static ConcurrentHashMap<String, Integer> adminUrlMap = new ConcurrentHashMap<>(128);
    @Resource
    private SysResourceService resourceService;

    public static boolean authority(UserVo user, String url) {
        if (user.getAdmin() == 1 || !needAuthority(url)) {
            return true;
        }

        if (adminUrlMap.containsKey(url)) return false;

        Set<Integer> ids = user.getResource_ids();

        if (ids == null) return false;

        return ids.contains(urlMap.get(url));
    }

    private static boolean needAuthority(String url) {
        return urlMap.containsKey(url) || adminUrlMap.containsKey(url);
    }

    @PostConstruct
    public void init() {
        Instant start = Instant.now();

        List<SysResource> resources = resourceService.getAll();

        if (resources != null) {
            for (SysResource p : resources) {
                if (p.getAdmin() == 1) {
                    adminUrlMap.put(p.getUrl(), p.getId());
                }
                else urlMap.put(p.getUrl(), p.getId());
            }
        }

        Instant end = Instant.now();

        log.info("权限模块启动成功，耗时：{}毫秒", ChronoUnit.MILLIS.between(start, end));
    }
}

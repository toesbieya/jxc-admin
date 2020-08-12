package cn.toesbieya.jxc.module;

import cn.toesbieya.jxc.model.entity.SysResource;
import cn.toesbieya.jxc.model.vo.UserVo;
import cn.toesbieya.jxc.service.sys.SysResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class PermissionModule implements CommandLineRunner {
    private final static ConcurrentHashMap<String, SysResource> urlMap = new ConcurrentHashMap<>(256);
    private final static ConcurrentHashMap<String, SysResource> adminUrlMap = new ConcurrentHashMap<>(16);
    @Resource
    private SysResourceService service;

    public static boolean authority(UserVo user, String url) {
        boolean isAdmin = user.isAdmin();
        SysResource r = adminUrlMap.get(url);

        //访问admin权限的资源时，需要用户是admin并且该权限已启用
        if (r != null) {
            return r.isEnable() && isAdmin;
        }

        if (isAdmin) return true;

        r = urlMap.get(url);

        //权限表中无记录的资源放行
        if (r == null) return true;

        //未启用时拦截
        if (!r.isEnable()) return false;

        //根据用户权限判断
        Set<Integer> ids = user.getResourceIds();
        return ids != null && ids.contains(r.getId());
    }

    @Override
    public void run(String... args) {
        Instant start = Instant.now();

        List<SysResource> resources = service.getEnableApi();

        for (SysResource p : resources) {
            String path = p.getPath();
            if (p.isAdmin()) {
                adminUrlMap.put(path, p);
            }
            else urlMap.put(path, p);
        }

        Instant end = Instant.now();
        log.info("权限资源加载完成，耗时：{}毫秒", ChronoUnit.MILLIS.between(start, end));
    }
}

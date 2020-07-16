package cn.toesbieya.jxc.gateway.config;

import cn.toesbieya.jxc.common.model.entity.SysResource;
import cn.toesbieya.jxc.common.model.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class ResourceConfig implements CommandLineRunner {
    private final static ConcurrentHashMap<String, Integer> urlMap = new ConcurrentHashMap<>(256);
    private final static ConcurrentHashMap<String, Integer> adminUrlMap = new ConcurrentHashMap<>(16);
    private static JdbcTemplate jdbcTemplate;

    public static boolean authority(UserVo user, String url) {
        //超级管理员放行
        if (user.getAdmin() == 1) {
            return true;
        }
        //普通用户访问admin权限的资源时拦截
        if (adminUrlMap.containsKey(url)) {
            return false;
        }
        //权限表中无记录的资源放行
        if (!urlMap.containsKey(url)) {
            return true;
        }

        Set<Integer> ids = user.getResource_ids();

        return ids != null && ids.contains(urlMap.get(url));
    }

    @Override
    public void run(String... args) throws Exception {
        init(false);
    }

    public static void init(boolean clear) {
        Instant start = Instant.now();

        if (clear) {
            urlMap.clear();
            adminUrlMap.clear();
        }

        String sql = "select id,pid,url,admin from sys_resource";

        List<SysResource> resources = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(SysResource.class));

        completeNode(resources);

        for (SysResource p : resources) {
            if (p.getAdmin().equals(1)) {
                adminUrlMap.put(p.getUrl(), p.getId());
            }
            else urlMap.put(p.getUrl(), p.getId());
        }

        Instant end = Instant.now();
        log.info("权限资源加载完成，耗时：{}毫秒", ChronoUnit.MILLIS.between(start, end));
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        ResourceConfig.jdbcTemplate = jdbcTemplate;
    }

    private static void completeNode(List<SysResource> list) {
        HashMap<Integer, String> url = new HashMap<>(128);

        for (SysResource resource : list) {
            //pid为0时跳过
            if (resource.getPid() == 0) {
                url.put(resource.getId(), resource.getUrl());
                continue;
            }

            //获取父节点进行拼接
            String parentUrl = url.get(resource.getPid());

            resource.setUrl(parentUrl + resource.getUrl());
            url.put(resource.getId(), resource.getUrl());
        }
    }
}

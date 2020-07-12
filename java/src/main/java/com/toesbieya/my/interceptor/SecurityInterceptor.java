package com.toesbieya.my.interceptor;

import com.toesbieya.my.model.vo.UserVo;
import com.toesbieya.my.module.PermissionModule;
import com.toesbieya.my.module.request.RequestModule;
import com.toesbieya.my.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class SecurityInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getServletPath();
        String method = request.getMethod();
        String ip = IpUtil.getIp(request);

        if (!RequestModule.urlExistMapping(url, method)) {
            WebUtil.responseJson(response, Result.notfound());
            log.error("请求地址不存在：{}，{}，IP：{}", url, method, ip);
            ThreadUtil.clearUser();
            return false;
        }

        UserVo user = SessionUtil.get(request);

        if (user == null) {
            WebUtil.responseJson(response, Result.requireLogin());
            ThreadUtil.clearUser();
            return false;
        }

        if (!PermissionModule.authority(user, url)) {
            WebUtil.responseJson(response, Result.noPermission());
            log.warn("权限拦截，访问路径：{}，用户：{}，IP:{}", url, user.getName(), ip);
            ThreadUtil.clearUser();
            return false;
        }

        return true;
    }
}

package com.toesbieya.my.interceptor;

import com.toesbieya.my.constant.SessionConstant;
import com.toesbieya.my.model.entity.SysUser;
import com.toesbieya.my.module.PermissionModule;
import com.toesbieya.my.module.request.RequestModule;
import com.toesbieya.my.utils.IpUtil;
import com.toesbieya.my.utils.Result;
import com.toesbieya.my.utils.ThreadUtil;
import com.toesbieya.my.utils.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
public class SecurityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession httpSession = request.getSession();
        String url = request.getServletPath();
        String method = request.getMethod();
        String ip = IpUtil.getIp(request);

        if (!RequestModule.urlExistMapping(url, method)) {
            Util.responseJson(response, Result.notfound());
            log.error("请求地址不存在：{}，{}，IP：{}", url, method, ip);
            ThreadUtil.clearUser();
            return false;
        }

        SysUser sysUser = Util.getUser(httpSession);
        if (sysUser == null
                || request.getHeader(SessionConstant.TOKEN_KEY) == null
                || !request.getHeader(SessionConstant.TOKEN_KEY).equals(httpSession.getAttribute(SessionConstant.TOKEN_KEY))) {
            Util.responseJson(response, Result.requireLogin());
            ThreadUtil.clearUser();
            return false;
        }

        if (!PermissionModule.authority(sysUser, url)) {
            Util.responseJson(response, Result.noPermission());
            log.warn("权限拦截，访问路径：{}，用户：{}，IP:{}", url, sysUser.getName(), ip);
            ThreadUtil.clearUser();
            return false;
        }

        ThreadUtil.setUser(sysUser);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}

package com.toesbieya.my.interceptor;

import com.toesbieya.my.model.entity.SysUser;
import com.toesbieya.my.module.request.RequestModule;
import com.toesbieya.my.utils.IpUtil;
import com.toesbieya.my.utils.Result;
import com.toesbieya.my.utils.ThreadUtil;
import com.toesbieya.my.utils.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class RateControlInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        SysUser user = ThreadUtil.getUser();
        if (user.getAdmin() == 1) return true;

        String url = request.getServletPath();
        String ip = IpUtil.getIp(request);
        int result = RequestModule.pass(url, ip);
        switch (result) {
            case -1:
                log.warn("限流拦截，服务的总请求频率超出，url：{}，用户：{}，ip：{}", url, user.getName(), ip);
                Util.responseJson(response, Result.overload());
                break;
            case 0:
                log.warn("限流拦截，ip已被暂时拉黑，url：{}，用户：{}，ip：{}", url, user.getName(), ip);
                Util.responseJson(response, Result.tooManyRequest());
                break;
            case -2:
                log.warn("限流拦截，ip的请求频率超出，url：{}，用户：{}，ip：{}", url, user.getName(), ip);
                Util.responseJson(response, Result.tooManyRequest());
                break;
            case 1:
                return true;
        }
        ThreadUtil.clearUser();
        return false;
    }
}

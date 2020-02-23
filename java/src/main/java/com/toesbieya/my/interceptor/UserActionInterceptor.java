package com.toesbieya.my.interceptor;

import com.toesbieya.my.utils.ThreadUtil;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserActionInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        ThreadUtil.quicklySetAction(request);
        return true;
    }
}

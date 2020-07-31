package cn.toesbieya.jxc.web.common.interceptor;

import cn.toesbieya.jxc.web.common.interceptor.predicate.UserActionPredicate;
import cn.toesbieya.jxc.web.common.utils.ThreadUtil;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UserActionInterceptor implements HandlerInterceptor {
    private final UserActionPredicate predicate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if (predicate.allowed(request)) {
            ThreadUtil.quicklySetAction(request);
        }
        return true;
    }

    public UserActionInterceptor(UserActionPredicate predicate) {
        this.predicate = predicate;
    }
}

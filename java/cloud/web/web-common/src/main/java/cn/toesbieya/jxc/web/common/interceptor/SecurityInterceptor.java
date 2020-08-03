package cn.toesbieya.jxc.web.common.interceptor;

import cn.toesbieya.jxc.common.model.vo.UserVo;
import cn.toesbieya.jxc.common.util.SessionUtil;
import cn.toesbieya.jxc.web.common.util.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class SecurityInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserVo user = SessionUtil.get(request);
        if (user == null) ThreadUtil.clearAll();
        else ThreadUtil.setUser(user);
        return true;
    }
}

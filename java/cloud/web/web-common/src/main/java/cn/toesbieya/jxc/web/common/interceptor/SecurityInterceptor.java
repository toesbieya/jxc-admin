package cn.toesbieya.jxc.web.common.interceptor;

import cn.toesbieya.jxc.common.model.vo.Result;
import cn.toesbieya.jxc.common.model.vo.UserVo;
import cn.toesbieya.jxc.common.utils.SessionUtil;
import cn.toesbieya.jxc.web.common.utils.ThreadUtil;
import cn.toesbieya.jxc.web.common.utils.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class SecurityInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserVo user = SessionUtil.get(request);

        if (user == null) {
            WebUtil.responseJson(response, Result.requireLogin());
            ThreadUtil.clearAll();
            return false;
        }

        ThreadUtil.setUser(user);

        return true;
    }
}

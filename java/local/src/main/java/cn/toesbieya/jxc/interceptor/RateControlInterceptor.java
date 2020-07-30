package cn.toesbieya.jxc.interceptor;

import cn.toesbieya.jxc.model.vo.UserVo;
import cn.toesbieya.jxc.module.request.RequestModule;
import cn.toesbieya.jxc.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class RateControlInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserVo user = SessionUtil.get(request);

        if (user.getAdmin() == 1) return true;

        String url = request.getServletPath();
        String ip = IpUtil.getIp(request);

        int result = RequestModule.pass(url, ip);

        switch (result) {
            case -1:
                log.warn("限流拦截，服务的总请求频率超出，url：{}，用户：{}，ip：{}", url, user.getName(), ip);
                WebUtil.responseJson(response, Result.overload());
                break;
            case 0:
                log.warn("限流拦截，ip已被暂时拉黑，url：{}，用户：{}，ip：{}", url, user.getName(), ip);
                WebUtil.responseJson(response, Result.tooManyRequest());
                break;
            case -2:
                log.warn("限流拦截，ip的请求频率超出，url：{}，用户：{}，ip：{}", url, user.getName(), ip);
                WebUtil.responseJson(response, Result.tooManyRequest());
                break;
            case 1:
                return true;
        }

        ThreadUtil.clearUser();

        return false;
    }
}

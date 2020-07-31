package cn.toesbieya.jxc.utils;

import cn.toesbieya.jxc.model.vo.Result;
import com.alibaba.fastjson.JSON;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

public class WebUtil {
    public static void responseJson(HttpServletResponse response, Result result) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");

        response.getWriter().print(JSON.toJSONString(result));
    }

    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }
}

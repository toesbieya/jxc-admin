package cn.toesbieya.jxc.web.common.utils;

import cn.toesbieya.jxc.common.model.vo.Result;
import com.alibaba.fastjson.JSON;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WebUtil {
    public static void responseJson(HttpServletResponse response, Result result) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");

        response.getWriter().print(JSON.toJSONString(result));
    }
}

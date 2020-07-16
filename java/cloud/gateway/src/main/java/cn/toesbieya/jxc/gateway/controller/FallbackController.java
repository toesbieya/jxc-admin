package cn.toesbieya.jxc.gateway.controller;

import cn.toesbieya.jxc.common.model.vo.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

public class FallbackController {
    @RequestMapping("fallback")
    @ResponseBody
    public Result fallback() {
        return Result.fail("服务异常");
    }
}

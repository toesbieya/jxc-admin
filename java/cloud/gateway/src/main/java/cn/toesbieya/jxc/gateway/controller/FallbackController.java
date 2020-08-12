package cn.toesbieya.jxc.gateway.controller;

import cn.toesbieya.jxc.common.model.vo.R;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

public class FallbackController {
    @RequestMapping("fallback")
    @ResponseBody
    public R fallback() {
        return R.fail("服务异常");
    }
}

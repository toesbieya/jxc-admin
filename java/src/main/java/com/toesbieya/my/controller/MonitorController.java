package com.toesbieya.my.controller;

import com.toesbieya.my.utils.MonitorUtil;
import com.toesbieya.my.utils.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("system/monitor")
public class MonitorController {

    @GetMapping("get")
    public Result get() {
        return Result.success(MonitorUtil.getMonitorInfo());
    }
}

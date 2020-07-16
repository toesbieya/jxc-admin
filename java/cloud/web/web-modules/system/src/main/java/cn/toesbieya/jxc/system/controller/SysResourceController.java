package cn.toesbieya.jxc.system.controller;

import cn.toesbieya.jxc.common.model.vo.Result;
import cn.toesbieya.jxc.system.service.SysResourceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("resource")
public class SysResourceController {
    @Resource
    private SysResourceService resourceService;

    @GetMapping("get")
    public Result get() {
        return Result.success(resourceService.get());
    }

    @GetMapping("getAll")
    public Result getAll() {
        return Result.success(resourceService.getAll());
    }
}

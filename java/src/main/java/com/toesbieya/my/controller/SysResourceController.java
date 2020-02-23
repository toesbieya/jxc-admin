package com.toesbieya.my.controller;

import com.toesbieya.my.model.entity.SysResource;
import com.toesbieya.my.service.SysResourceService;
import com.toesbieya.my.utils.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("system/resource")
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

    @PostMapping("update")
    public Result update(@RequestBody SysResource resource) {
        if (resource.getId() == null
                || resource.getTotal_rate() == null
                || resource.getIp_rate() == null) {
            return Result.fail("参数错误");
        }
        return resourceService.update(resource);
    }
}

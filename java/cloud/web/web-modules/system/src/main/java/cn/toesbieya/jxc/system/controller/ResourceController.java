package cn.toesbieya.jxc.system.controller;

import cn.toesbieya.jxc.common.model.entity.SysResource;
import cn.toesbieya.jxc.common.model.vo.Result;
import cn.toesbieya.jxc.system.service.ResourceService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("resource")
public class ResourceController {
    @Resource
    private ResourceService service;

    @GetMapping("getAll")
    public Result getAll() {
        return Result.success(service.getAll());
    }

    @PostMapping("update")
    public Result update(@RequestBody SysResource resource) {
        if (resource.getId() == null
                || resource.getTotalRate() == null
                || resource.getIpRate() == null) {
            return Result.fail("参数错误");
        }
        return service.update(resource);
    }
}

package cn.toesbieya.jxc.system.controller;

import cn.toesbieya.jxc.common.model.entity.SysResource;
import cn.toesbieya.jxc.common.model.vo.R;
import cn.toesbieya.jxc.system.service.ResourceService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("resource")
public class ResourceController {
    @Resource
    private ResourceService service;

    @GetMapping("getAll")
    public R getAll() {
        return R.success(service.getAll());
    }

    @PostMapping("add")
    public R add(@RequestBody SysResource entity) {
        return service.add(entity);
    }

    @PostMapping("update")
    public R update(@RequestBody SysResource entity) {
        return service.update(entity);
    }

    @GetMapping("del")
    public R del(@RequestParam int id) {
        return service.del(id);
    }
}

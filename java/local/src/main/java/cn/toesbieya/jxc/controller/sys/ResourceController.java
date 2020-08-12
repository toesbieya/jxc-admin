package cn.toesbieya.jxc.controller.sys;

import cn.toesbieya.jxc.model.entity.SysResource;
import cn.toesbieya.jxc.model.vo.R;
import cn.toesbieya.jxc.service.sys.SysResourceService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("system/resource")
public class ResourceController {
    @Resource
    private SysResourceService service;

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

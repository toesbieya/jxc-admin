package cn.toesbieya.jxc.controller.sys;

import cn.toesbieya.jxc.model.entity.SysResource;
import cn.toesbieya.jxc.service.sys.SysResourceService;
import cn.toesbieya.jxc.model.vo.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("system/resource")
public class ResourceController {
    @Resource
    private SysResourceService service;

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

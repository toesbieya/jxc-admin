package cn.toesbieya.jxc.controller.sys;

import cn.toesbieya.jxc.model.vo.Result;
import cn.toesbieya.jxc.service.sys.SysResourceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

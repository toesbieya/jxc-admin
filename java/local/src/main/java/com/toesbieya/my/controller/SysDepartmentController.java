package com.toesbieya.my.controller;

import com.toesbieya.my.model.entity.SysDepartment;
import com.toesbieya.my.service.SysDepartmentService;
import com.toesbieya.my.utils.Result;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("system/department")
public class SysDepartmentController {
    @Resource
    private SysDepartmentService departmentService;

    @GetMapping("get")
    public Result get() {
        return Result.success(departmentService.get());
    }

    @GetMapping("getAll")
    public Result getAll() {
        return Result.success(departmentService.getAll());
    }

    @PostMapping("add")
    public Result add(@RequestBody SysDepartment department) {
        if (null == department.getPid()
                || StringUtils.isEmpty(department.getName())
                || null == department.getStatus()) {
            return Result.fail("添加失败，参数错误");
        }
        return departmentService.add(department);
    }

    @PostMapping("update")
    public Result update(@RequestBody SysDepartment department) {
        if (null == department.getId()
                || null == department.getPid()
                || StringUtils.isEmpty(department.getName())
                || null == department.getStatus()) {
            return Result.fail("修改失败，参数错误");
        }
        return departmentService.update(department);
    }

    @PostMapping("del")
    public Result del(@RequestBody SysDepartment department) {
        if (null == department.getId() || StringUtils.isEmpty(department.getName())) {
            return Result.fail("删除失败，参数错误");
        }
        return departmentService.del(department);
    }
}

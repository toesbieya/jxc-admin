package com.toesbieya.my.controller;

import com.toesbieya.my.model.entity.SysRole;
import com.toesbieya.my.model.entity.SysUser;
import com.toesbieya.my.model.vo.search.RoleSearch;
import com.toesbieya.my.service.SysRoleService;
import com.toesbieya.my.utils.Result;
import com.toesbieya.my.utils.Util;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RequestMapping("system/role")
@RestController
public class SysRoleController {
    @Resource
    private SysRoleService roleService;
    @Resource
    private HttpSession session;

    @GetMapping("get")
    public Result get() {
        return Result.success(roleService.get());
    }

    @PostMapping("search")
    public Result search(@RequestBody RoleSearch vo) {
        return Result.success(roleService.search(vo));
    }

    @PostMapping("add")
    public Result add(@RequestBody SysRole role) {
        if (StringUtils.isEmpty(role.getName())
                || role.getStatus() == null) {
            return Result.fail("添加失败，参数错误");
        }

        SysUser user = Util.getUser();
        role.setCid(user.getId());
        role.setCname(user.getName());
        role.setCtime(System.currentTimeMillis());

        return roleService.add(role);
    }

    @PostMapping("update")
    public Result update(@RequestBody SysRole role) {
        if (StringUtils.isEmpty(role.getName())
                || role.getId() == null
                || role.getStatus() == null) {
            return Result.fail("修改失败，参数错误");
        }
        return roleService.update(role);
    }

    @PostMapping("del")
    public Result del(@RequestBody SysRole role) {
        if (role.getId() == null) return Result.fail("删除失败，参数错误");
        return roleService.del(role);
    }
}

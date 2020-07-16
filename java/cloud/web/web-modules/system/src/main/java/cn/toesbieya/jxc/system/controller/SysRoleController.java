package cn.toesbieya.jxc.system.controller;

import cn.toesbieya.jxc.common.model.entity.SysRole;
import cn.toesbieya.jxc.common.model.entity.SysUser;
import cn.toesbieya.jxc.common.model.vo.Result;
import cn.toesbieya.jxc.common.utils.SessionUtil;
import cn.toesbieya.jxc.system.model.vo.RoleSearch;
import cn.toesbieya.jxc.system.service.SysRoleService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RequestMapping("role")
@RestController
public class SysRoleController {
    @Resource
    private SysRoleService roleService;

    @GetMapping("get")
    public Result get() {
        return Result.success(roleService.get());
    }

    @PostMapping("search")
    public Result search(@RequestBody RoleSearch vo) {
        return Result.success(roleService.search(vo));
    }

    @PostMapping("add")
    public Result add(HttpServletRequest request, @RequestBody SysRole role) {
        if (StringUtils.isEmpty(role.getName())
                || role.getStatus() == null) {
            return Result.fail("添加失败，参数错误");
        }

        SysUser user = SessionUtil.get(request);

        role.setId(null);
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
        if (role.getId() == null) {
            return Result.fail("删除失败，参数错误");
        }

        return roleService.del(role);
    }
}

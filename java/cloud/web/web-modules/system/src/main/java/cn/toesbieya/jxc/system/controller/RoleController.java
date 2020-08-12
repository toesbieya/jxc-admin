package cn.toesbieya.jxc.system.controller;

import cn.toesbieya.jxc.common.model.entity.SysRole;
import cn.toesbieya.jxc.common.model.entity.SysUser;
import cn.toesbieya.jxc.common.model.vo.R;
import cn.toesbieya.jxc.system.model.vo.RoleSearch;
import cn.toesbieya.jxc.system.service.RoleService;
import cn.toesbieya.jxc.web.common.util.SessionUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RequestMapping("role")
@RestController
public class RoleController {
    @Resource
    private RoleService service;

    @GetMapping("get")
    public R get() {
        return R.success(service.get());
    }

    @PostMapping("search")
    public R search(@RequestBody RoleSearch vo) {
        return R.success(service.search(vo));
    }

    @PostMapping("add")
    public R add(@RequestBody SysRole role) {
        if (StringUtils.isEmpty(role.getName())
                || role.getStatus() == null) {
            return R.fail("添加失败，参数错误");
        }

        SysUser user = SessionUtil.get();

        role.setId(null);
        role.setCid(user.getId());
        role.setCname(user.getName());
        role.setCtime(System.currentTimeMillis());

        return service.add(role);
    }

    @PostMapping("update")
    public R update(@RequestBody SysRole role) {
        if (StringUtils.isEmpty(role.getName())
                || role.getId() == null
                || role.getStatus() == null) {
            return R.fail("修改失败，参数错误");
        }

        return service.update(role);
    }

    @PostMapping("del")
    public R del(@RequestBody SysRole role) {
        if (role.getId() == null) {
            return R.fail("删除失败，参数错误");
        }

        return service.del(role);
    }
}

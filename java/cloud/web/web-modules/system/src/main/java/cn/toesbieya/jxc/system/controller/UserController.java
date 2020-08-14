package cn.toesbieya.jxc.system.controller;

import cn.toesbieya.jxc.common.model.entity.RecUserAction;
import cn.toesbieya.jxc.common.model.entity.SysUser;
import cn.toesbieya.jxc.common.model.vo.R;
import cn.toesbieya.jxc.system.model.vo.UserSearch;
import cn.toesbieya.jxc.system.service.UserService;
import cn.toesbieya.jxc.web.common.util.ThreadUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("user")
public class UserController {
    @Resource
    private UserService service;

    @PostMapping("search")
    public R search(@RequestBody UserSearch vo) {
        return R.success(service.search(vo));
    }

    @PostMapping("add")
    public R add(@RequestBody SysUser user) {
        String errMsg = validateUserCreateParam(user);
        if (errMsg != null) {
            return R.fail(errMsg);
        }
        return service.add(user);
    }

    @PostMapping("update")
    public R update(@RequestBody SysUser user) {
        String errMsg = validateUserUpdateParam(user);
        if (errMsg != null) {
            return R.fail(errMsg);
        }
        return service.update(user);
    }

    @PostMapping("del")
    public R del(@RequestBody SysUser user) {
        if (user.getId() == null) {
            return R.fail("删除失败");
        }
        return service.del(user);
    }

    @PostMapping("kick")
    public R kick(@RequestBody List<SysUser> users) {
        if (users == null || users.isEmpty()) {
            return R.fail("参数错误");
        }
        RecUserAction action = ThreadUtil.getAction();
        List<String> names = users.stream().map(SysUser::getName).collect(Collectors.toList());
        action.setAction("踢出用户：【" + String.join(",", names) + "】");

        return service.kick(users);
    }

    @PostMapping("resetPwd")
    public R resetPwd(@RequestBody SysUser user) {
        if (user.getId() == null || StringUtils.isEmpty(user.getName())) {
            return R.fail("参数错误");
        }
        return service.resetPwd(user);
    }

    private String validateUserCreateParam(SysUser user) {
        if (user.getId() != null) return "创建失败，参数错误";
        if (StringUtils.isEmpty(user.getName())) return "创建失败，用户名称不能为空";
        if (user.getRole() == null) return "创建失败，用户角色不能为空";
        return null;
    }

    private String validateUserUpdateParam(SysUser user) {
        if (user.getId() == null) return "修改失败，参数错误";
        if (StringUtils.isEmpty(user.getName())) return "修改失败，用户名称不能为空";
        if (user.getRole() == null) return "修改失败，用户角色不能为空";
        return null;
    }
}

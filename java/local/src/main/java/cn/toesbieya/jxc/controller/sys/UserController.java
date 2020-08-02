package cn.toesbieya.jxc.controller.sys;

import cn.toesbieya.jxc.model.entity.RecUserAction;
import cn.toesbieya.jxc.model.entity.SysUser;
import cn.toesbieya.jxc.model.vo.search.UserSearch;
import cn.toesbieya.jxc.utils.ThreadUtil;
import cn.toesbieya.jxc.service.sys.SysUserService;
import cn.toesbieya.jxc.model.vo.Result;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController("sysUserController")
@RequestMapping("system/user")
public class UserController {
    @Resource
    private SysUserService service;

    @PostMapping("search")
    public Result search(@RequestBody UserSearch vo) {
        return Result.success(service.search(vo));
    }

    @PostMapping("add")
    public Result add(@RequestBody SysUser user) {
        String errMsg = validateUserCreateParam(user);
        if (errMsg != null) {
            return Result.fail(errMsg);
        }
        return service.add(user);
    }

    @PostMapping("update")
    public Result update(@RequestBody SysUser user) {
        String errMsg = validateUserUpdateParam(user);
        if (errMsg != null) {
            return Result.fail(errMsg);
        }
        return service.update(user);
    }

    @PostMapping("del")
    public Result del(@RequestBody SysUser user) {
        if (user.getId() == null) {
            return Result.fail("删除失败");
        }
        return service.del(user);
    }

    @PostMapping("kick")
    public Result kick(@RequestBody List<SysUser> users) {
        if (users == null || users.isEmpty()) {
            return Result.fail("参数错误");
        }
        RecUserAction action = ThreadUtil.getAction();
        List<String> names = users.stream().map(SysUser::getName).collect(Collectors.toList());
        action.setAction("踢出用户：【" + String.join(",", names) + "】");

        return service.kick(users);
    }

    @PostMapping("resetPwd")
    public Result resetPwd(@RequestBody SysUser user) {
        if (user.getId() == null || StringUtils.isEmpty(user.getName())) {
            return Result.fail("参数错误");
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
        if (user.getStatus() == null) return "修改失败，用户状态不能为空";
        return null;
    }
}

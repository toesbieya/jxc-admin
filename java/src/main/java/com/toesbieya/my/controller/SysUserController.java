package com.toesbieya.my.controller;

import com.toesbieya.my.model.entity.RecUserAction;
import com.toesbieya.my.model.entity.SysUser;
import com.toesbieya.my.model.vo.search.LoginHistorySearch;
import com.toesbieya.my.model.vo.search.UserActionSearch;
import com.toesbieya.my.model.vo.search.UserSearch;
import com.toesbieya.my.model.vo.update.UserUpdatePwd;
import com.toesbieya.my.service.RecService;
import com.toesbieya.my.service.SysUserService;
import com.toesbieya.my.utils.DateUtil;
import com.toesbieya.my.utils.Result;
import com.toesbieya.my.utils.ThreadUtil;
import com.toesbieya.my.utils.Util;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("system/user")
public class SysUserController {
    @Resource
    private SysUserService userService;
    @Resource
    private RecService recService;

    @PostMapping("search")
    public Result search(@RequestBody UserSearch vo) {
        return Result.success(userService.search(vo));
    }

    @PostMapping("getLoginHistory")
    public Result getLoginHistory(@RequestBody LoginHistorySearch vo) {
        SysUser user = Util.getUser();
        vo.setUid(user.getId());
        vo.setStartTime(DateUtil.getTimestampBeforeNow(7));

        return Result.success(recService.searchLoginHistory(vo));
    }

    @PostMapping("getUserAction")
    public Result getUserAction(@RequestBody UserActionSearch vo) {
        SysUser user = Util.getUser();
        vo.setUid(user.getId());
        vo.setStartTime(DateUtil.getTimestampBeforeNow(7));

        return Result.success(recService.searchUserAction(vo));
    }

    @PostMapping("add")
    public Result add(@RequestBody SysUser user) {
        String errMsg = validateUserCreateParam(user);
        if (errMsg != null) {
            return Result.fail(errMsg);
        }
        return userService.add(user);
    }

    @PostMapping("update")
    public Result update(@RequestBody SysUser user) {
        String errMsg = validateUserUpdateParam(user);
        if (errMsg != null) {
            return Result.fail(errMsg);
        }
        userService.setUpdateAction(user);
        return userService.update(user);
    }

    @PostMapping("del")
    public Result del(@RequestBody SysUser user) {
        if (user.getId() == null) {
            return Result.fail("删除失败");
        }
        return userService.del(user);
    }

    @PostMapping("kick")
    public Result kick(@RequestBody List<SysUser> users) {
        if (users == null || users.isEmpty()) {
            return Result.fail("参数错误");
        }
        RecUserAction action = ThreadUtil.getAction();
        List<String> names = users.stream().map(SysUser::getName).collect(Collectors.toList());
        action.setAction("踢出用户：【" + String.join(",", names) + "】");

        return userService.kick(users);
    }

    @PostMapping("updatePwd")
    public Result updatePwd(@RequestBody UserUpdatePwd vo) {
        SysUser user = Util.getUser();
        vo.setId(user.getId());

        String errMsg = validateUpdatePwdParam(vo);
        if (errMsg != null) return Result.fail(errMsg);

        vo.setOld_pwd(DigestUtils.md5DigestAsHex(vo.getOld_pwd().getBytes()));
        vo.setNew_pwd(DigestUtils.md5DigestAsHex(vo.getNew_pwd().getBytes()));

        return userService.updatePwd(vo);
    }

    @PostMapping("resetPwd")
    public Result resetPwd(@RequestBody SysUser user) {
        if (user.getId() == null || StringUtils.isEmpty(user.getName())) {
            return Result.fail("参数错误");
        }
        return userService.resetPwd(user);
    }

    @GetMapping("updateAvatar")
    public Result updateAvatar(@RequestParam String key) throws UnsupportedEncodingException {
        if (StringUtils.isEmpty(key)) return Result.fail("参数错误");

        return userService.updateAvatar(Util.getUser(), URLDecoder.decode(key, "utf-8"), Util.getSession());
    }

    @GetMapping("validate")
    public Result validate(@RequestParam String pwd) {
        SysUser current = Util.getUser();
        if (!pwd.equals(current.getPwd())) return Result.fail("校验失败");
        return Result.success("校验通过");
    }

    @GetMapping("checkName")
    public Result checkName(@RequestParam String name) {
        return Result.success(userService.checkName(name) ? null : "该用户名已存在");
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

    private String validateUpdatePwdParam(UserUpdatePwd vo) {
        if (vo.getId() == null) return "修改失败，参数错误";
        if (StringUtils.isEmpty(vo.getOld_pwd())) return "修改失败，原密码不能为空";
        if (StringUtils.isEmpty(vo.getNew_pwd())) return "修改失败，新密码不能为空";
        if (vo.getOld_pwd().equals(vo.getNew_pwd())) return "修改失败，新密码不得与旧密码相同";
        if (vo.getNew_pwd().length() < 6 || vo.getNew_pwd().length() > 32) return "修改失败，密码长度为6-32位";
        return null;
    }
}

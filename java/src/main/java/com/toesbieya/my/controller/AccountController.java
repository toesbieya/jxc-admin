package com.toesbieya.my.controller;

import com.toesbieya.my.enumeration.RecLoginHistoryEnum;
import com.toesbieya.my.model.entity.SysUser;
import com.toesbieya.my.model.vo.update.UserUpdatePwd;
import com.toesbieya.my.service.RecService;
import com.toesbieya.my.service.SysUserService;
import com.toesbieya.my.utils.IpUtil;
import com.toesbieya.my.utils.Result;
import com.toesbieya.my.utils.Util;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

@RestController
@RequestMapping("account")
public class AccountController {
    @Resource
    private SysUserService userService;

    @Resource
    private RecService recService;

    @Resource
    private HttpSession session;

    @PostMapping("login")
    public Result login(HttpServletRequest request, @RequestBody Map<String, String> map) {
        String username = map.get("username");
        String password = map.get("password");
        String err = validate(username, password);
        if (err != null) return Result.fail(err);

        return userService.login(username, password, IpUtil.getIp(request));
    }

    @PostMapping("register")
    public Result register(@RequestBody Map<String, String> map) {
        String username = map.get("username");
        String password = map.get("password");
        String err = validate(username, password);
        if (err != null) return Result.fail(err);

        return userService.register(username, password);
    }

    @GetMapping("logout")
    public Result logout(HttpServletRequest request) {
        SysUser user = Util.getUser();
        if (user != null) {
            recService.insertLoginHistory(user, IpUtil.getIp(request), RecLoginHistoryEnum.LOGOUT);
        }
        session.invalidate();
        return Result.success("登出成功");
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

    private String validate(String username, String password) {
        if ((StringUtils.isEmpty(username)
                || StringUtils.isEmpty(password)
                || username.length() > 20
                || password.length() != 32)) {
            return "用户名或密码输入有误";
        }
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

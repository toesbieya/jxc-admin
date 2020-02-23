package com.toesbieya.my.controller;

import com.toesbieya.my.enumeration.RecLoginHistoryEnum;
import com.toesbieya.my.model.entity.SysUser;
import com.toesbieya.my.service.RecService;
import com.toesbieya.my.service.SysUserService;
import com.toesbieya.my.utils.IpUtil;
import com.toesbieya.my.utils.Result;
import com.toesbieya.my.utils.Util;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
public class LoginController {
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
        SysUser user = Util.getUser(session);
        if (user != null) {
            recService.insertLoginHistory(user, IpUtil.getIp(request), RecLoginHistoryEnum.LOGOUT);
        }
        session.invalidate();
        return Result.success("登出成功");
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
}

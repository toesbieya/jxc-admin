package cn.toesbieya.jxc.controller;

import cn.toesbieya.jxc.model.vo.LoginParam;
import cn.toesbieya.jxc.model.vo.PasswordUpdateParam;
import cn.toesbieya.jxc.model.vo.RegisterParam;
import cn.toesbieya.jxc.model.vo.UserVo;
import cn.toesbieya.jxc.service.AccountService;
import cn.toesbieya.jxc.util.SessionUtil;
import cn.toesbieya.jxc.util.IpUtil;
import cn.toesbieya.jxc.model.vo.Result;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@RestController
@RequestMapping("account")
public class AccountController {
    @Resource
    private AccountService service;

    @PostMapping("login")
    public Result login(HttpServletRequest request, @Valid @RequestBody LoginParam param) {
        return service.login(param, IpUtil.getIp(request));
    }

    @GetMapping("logout")
    public Result logout(HttpServletRequest request) {
        UserVo user = SessionUtil.get();
        return service.logout(user, IpUtil.getIp(request));
    }

    @PostMapping("register")
    public Result register(@Valid @RequestBody RegisterParam param) {
        return service.register(param);
    }

    @PostMapping("updatePwd")
    public Result updatePwd(@RequestBody PasswordUpdateParam param) {
        UserVo user = SessionUtil.get();
        param.setId(user.getId());

        String errMsg = validateUpdatePwdParam(param);
        if (errMsg != null) return Result.fail(errMsg);

        return service.updatePwd(param);
    }

    @GetMapping("updateAvatar")
    public Result updateAvatar(@RequestParam String key) throws UnsupportedEncodingException {
        if (StringUtils.isEmpty(key)) return Result.fail("参数错误");

        UserVo user = SessionUtil.get();
        return service.updateAvatar(user, URLDecoder.decode(key, "utf-8"));
    }

    @GetMapping("validate")
    public Result validate(@RequestParam String pwd) {
        UserVo current = SessionUtil.get();

        if (!pwd.equals(current.getPwd())) {
            return Result.fail("校验失败");
        }

        return Result.success("校验通过");
    }

    @GetMapping("checkName")
    public Result checkName(@RequestParam(required = false) Integer id, @RequestParam String name) {
        if (StringUtils.isEmpty(name)) {
            return Result.success();
        }

        return Result.success(service.checkName(name, id) ? null : "该用户名已存在");
    }

    private String validateUpdatePwdParam(PasswordUpdateParam vo) {
        if (vo.getId() == null) return "修改失败，参数错误";
        if (StringUtils.isEmpty(vo.getOldPwd())) return "修改失败，原密码不能为空";
        if (StringUtils.isEmpty(vo.getNewPwd())) return "修改失败，新密码不能为空";
        if (vo.getOldPwd().equals(vo.getNewPwd())) return "修改失败，新密码不得与旧密码相同";
        if (vo.getNewPwd().length() != 32) return "修改失败，密码参数有误";
        return null;
    }
}

package cn.toesbieya.jxc.account.controller;

import cn.toesbieya.jxc.account.service.AccountService;
import cn.toesbieya.jxc.account.vo.LoginParam;
import cn.toesbieya.jxc.account.vo.PasswordUpdateParam;
import cn.toesbieya.jxc.account.vo.RegisterParam;
import cn.toesbieya.jxc.common.model.entity.SysUser;
import cn.toesbieya.jxc.common.model.vo.UserVo;
import cn.toesbieya.jxc.common.model.vo.Result;
import cn.toesbieya.jxc.common.utils.SessionUtil;
import cn.toesbieya.jxc.web.common.utils.IpUtil;
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
    private AccountService accountService;

    @PostMapping("login")
    public Result login(HttpServletRequest request, @Valid @RequestBody LoginParam param) {
        return accountService.login(param, IpUtil.getIp(request));
    }

    @GetMapping("logout")
    public Result logout(HttpServletRequest request) {
        UserVo user = SessionUtil.get(request);
        return accountService.logout(user, IpUtil.getIp(request));
    }

    @PostMapping("register")
    public Result register(@Valid @RequestBody RegisterParam param) {
        return accountService.register(param);
    }

    @PostMapping("updatePwd")
    public Result updatePwd(HttpServletRequest request, @RequestBody PasswordUpdateParam param) {
        SysUser user = SessionUtil.get(request);
        param.setId(user.getId());

        String errMsg = validateUpdatePwdParam(param);
        if (errMsg != null) return Result.fail(errMsg);

        return accountService.updatePwd(param);
    }

    @GetMapping("updateAvatar")
    public Result updateAvatar(HttpServletRequest request, @RequestParam String key) throws UnsupportedEncodingException {
        if (StringUtils.isEmpty(key)) return Result.fail("参数错误");

        UserVo user = SessionUtil.get(request);
        return accountService.updateAvatar(user, URLDecoder.decode(key, "utf-8"));
    }

    @GetMapping("validate")
    public Result validate(HttpServletRequest request, @RequestParam String pwd) {
        SysUser current = SessionUtil.get(request);

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

        return Result.success(accountService.checkName(name, id) ? null : "该用户名已存在");
    }

    private String validateUpdatePwdParam(PasswordUpdateParam vo) {
        if (vo.getId() == null) return "修改失败，参数错误";
        if (StringUtils.isEmpty(vo.getOld_pwd())) return "修改失败，原密码不能为空";
        if (StringUtils.isEmpty(vo.getNew_pwd())) return "修改失败，新密码不能为空";
        if (vo.getOld_pwd().equals(vo.getNew_pwd())) return "修改失败，新密码不得与旧密码相同";
        if (vo.getNew_pwd().length() != 32) return "修改失败，密码参数有误";
        return null;
    }
}

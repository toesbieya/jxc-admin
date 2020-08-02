package cn.toesbieya.jxc.message.controller;

import cn.toesbieya.jxc.common.model.entity.SysUser;
import cn.toesbieya.jxc.common.model.vo.Result;
import cn.toesbieya.jxc.message.model.vo.MsgPersonalSearch;
import cn.toesbieya.jxc.message.service.UserService;
import cn.toesbieya.jxc.web.common.utils.SessionUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("user")
public class UserController {
    @Resource
    private UserService service;

    @PostMapping("search")
    public Result search(@RequestBody MsgPersonalSearch vo) {
        SysUser user = SessionUtil.get();
        vo.setUid(user.getId());
        vo.setCtime(user.getCtime());
        return Result.success(service.search(vo));
    }

    @GetMapping("read")
    public Result read(@RequestParam Integer id) {
        return service.read(SessionUtil.get(), id);
    }

    @GetMapping("readAll")
    public Result readAll() {
        return service.readAll(SessionUtil.get());
    }
}

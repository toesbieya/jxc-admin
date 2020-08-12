package cn.toesbieya.jxc.message.controller;

import cn.toesbieya.jxc.common.model.entity.SysUser;
import cn.toesbieya.jxc.common.model.vo.R;
import cn.toesbieya.jxc.message.model.vo.MsgPersonalSearch;
import cn.toesbieya.jxc.message.service.UserService;
import cn.toesbieya.jxc.web.common.util.SessionUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("user")
public class UserController {
    @Resource
    private UserService service;

    @PostMapping("search")
    public R search(@RequestBody MsgPersonalSearch vo) {
        SysUser user = SessionUtil.get();
        vo.setUid(user.getId());
        vo.setCtime(user.getCtime());
        return R.success(service.search(vo));
    }

    @GetMapping("read")
    public R read(@RequestParam Integer id) {
        return service.read(SessionUtil.get(), id);
    }

    @GetMapping("readAll")
    public R readAll() {
        return service.readAll(SessionUtil.get());
    }
}

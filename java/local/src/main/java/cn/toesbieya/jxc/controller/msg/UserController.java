package cn.toesbieya.jxc.controller.msg;

import cn.toesbieya.jxc.model.vo.R;
import cn.toesbieya.jxc.model.vo.UserVo;
import cn.toesbieya.jxc.model.vo.search.MsgPersonalSearch;
import cn.toesbieya.jxc.service.msg.MsgUserService;
import cn.toesbieya.jxc.util.SessionUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController("msgUserController")
@RequestMapping("message/user")
public class UserController {
    @Resource
    private MsgUserService service;

    @PostMapping("search")
    public R searchPersonal(@RequestBody MsgPersonalSearch vo) {
        UserVo user = SessionUtil.get();
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

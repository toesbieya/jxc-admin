package cn.toesbieya.jxc.message.controller;

import cn.toesbieya.jxc.common.model.entity.SysUser;
import cn.toesbieya.jxc.common.model.vo.Result;
import cn.toesbieya.jxc.common.utils.SessionUtil;
import cn.toesbieya.jxc.message.model.vo.MsgPersonalSearch;
import cn.toesbieya.jxc.message.service.MsgUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("user")
public class MsgUserController {
    @Resource
    private MsgUserService msgUserService;

    @PostMapping("search")
    public Result search(HttpServletRequest request, @RequestBody MsgPersonalSearch vo) {
        SysUser user = SessionUtil.get(request);
        vo.setUid(user.getId());
        vo.setCtime(user.getCtime());
        return Result.success(msgUserService.search(vo));
    }

    @GetMapping("read")
    public Result read(HttpServletRequest request, @RequestParam Integer id) {
        return msgUserService.read(SessionUtil.get(request), id);
    }

    @GetMapping("readAll")
    public Result readAll(HttpServletRequest request) {
        return msgUserService.readAll(SessionUtil.get(request));
    }
}

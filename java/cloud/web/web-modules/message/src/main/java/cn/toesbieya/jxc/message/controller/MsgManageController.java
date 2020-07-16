package cn.toesbieya.jxc.message.controller;

import cn.toesbieya.jxc.common.model.entity.Msg;
import cn.toesbieya.jxc.common.model.entity.SysUser;
import cn.toesbieya.jxc.common.model.vo.Result;
import cn.toesbieya.jxc.common.utils.SessionUtil;
import cn.toesbieya.jxc.message.constant.MsgConstant;
import cn.toesbieya.jxc.message.model.vo.MsgSearch;
import cn.toesbieya.jxc.message.service.MsgManageService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("manage")
public class MsgManageController {
    @Resource
    private MsgManageService msgManageService;

    @RequestMapping("search")
    public Result search(@RequestBody MsgSearch vo) {
        return Result.success(msgManageService.search(vo));
    }

    @PostMapping("add")
    public Result add(HttpServletRequest request, @RequestBody Msg msg) {
        String errMsg = validateAdd(msg);
        if (errMsg != null) return Result.fail(errMsg);

        msg.setId(null);
        SysUser user = SessionUtil.get(request);
        setAddInfo(user, msg);

        return msgManageService.add(msg);
    }

    @PostMapping("update")
    public Result update(@RequestBody Msg msg) {
        String errMsg = validateAdd(msg);
        if (errMsg == null) errMsg = validateUpdate(msg);
        if (errMsg != null) return Result.fail(errMsg);

        return msgManageService.update(msg);
    }

    @PostMapping("publish")
    public Result publish(HttpServletRequest request, @RequestBody Msg msg) {
        boolean isFirstCreate = msg.getId() == null;

        String errMsg = validateAdd(msg);
        if (errMsg == null && !isFirstCreate) {
            errMsg = validateUpdate(msg);
        }
        if (errMsg != null) return Result.fail(errMsg);

        SysUser user = SessionUtil.get(request);
        if (isFirstCreate) setAddInfo(user, msg);

        msg.setPid(user.getId());
        msg.setPname(user.getName());
        msg.setPtime(System.currentTimeMillis());
        msg.setStatus(MsgConstant.STATUS_PUBLISHED);

        return msgManageService.publish(msg);
    }

    @PostMapping("withdraw")
    public Result withdraw(HttpServletRequest request, @RequestBody Msg msg) {
        if (msg.getId() == null) return Result.fail("参数错误");

        SysUser user = SessionUtil.get(request);
        msg.setWid(user.getId());
        msg.setWname(user.getName());
        msg.setWtime(System.currentTimeMillis());
        msg.setStatus(MsgConstant.STATUS_WITHDREW);

        return msgManageService.withdraw(msg);
    }

    @GetMapping("del")
    public Result del(@RequestParam Integer id, @RequestParam String title) {
        return msgManageService.del(id, title);
    }

    private void setAddInfo(SysUser user, Msg msg) {
        msg.setCid(user.getId());
        msg.setCname(user.getName());
        msg.setCtime(System.currentTimeMillis());
        msg.setStatus(MsgConstant.STATUS_DRAFT);
    }

    private String validateAdd(Msg msg) {
        if (StringUtils.isEmpty(msg.getTitle())
                || msg.getType() == null
                || msg.getBroadcast() == null
                || msg.getBroadcast().equals(MsgConstant.TO_RANGE) && StringUtils.isEmpty(msg.getRecipient())
        ) return "参数错误";
        return null;
    }

    private String validateUpdate(Msg msg) {
        if (StringUtils.isEmpty(msg.getId())
                || !msg.getStatus().equals(MsgConstant.STATUS_DRAFT)
        ) return "参数错误";
        return null;
    }
}

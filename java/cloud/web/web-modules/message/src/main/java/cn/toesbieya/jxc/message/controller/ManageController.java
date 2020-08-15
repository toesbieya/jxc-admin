package cn.toesbieya.jxc.message.controller;

import cn.toesbieya.jxc.common.model.entity.Msg;
import cn.toesbieya.jxc.common.model.entity.SysUser;
import cn.toesbieya.jxc.common.model.vo.R;
import cn.toesbieya.jxc.common.model.vo.UserVo;
import cn.toesbieya.jxc.message.constant.MsgConstant;
import cn.toesbieya.jxc.message.model.vo.MsgSearch;
import cn.toesbieya.jxc.message.service.ManageService;
import cn.toesbieya.jxc.web.common.util.SessionUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("manage")
public class ManageController {
    @Resource
    private ManageService service;

    @RequestMapping("search")
    public R search(@RequestBody MsgSearch vo) {
        return R.success(service.search(vo));
    }

    @PostMapping("add")
    public R add(@RequestBody Msg msg) {
        String errMsg = validateAdd(msg);
        if (errMsg != null) return R.fail(errMsg);

        SysUser user = SessionUtil.get();
        setAddInfo(user, msg);

        return service.add(msg);
    }

    @PostMapping("update")
    public R update(@RequestBody Msg msg) {
        String errMsg = validateAdd(msg);
        if (errMsg == null) errMsg = validateUpdate(msg);
        if (errMsg != null) return R.fail(errMsg);

        return service.update(msg);
    }

    @PostMapping("publish")
    public R publish(@RequestBody Msg msg) {
        boolean isFirstCreate = msg.getId() == null;

        String errMsg = validateAdd(msg);
        if (errMsg == null && !isFirstCreate) {
            errMsg = validateUpdate(msg);
        }
        if (errMsg != null) return R.fail(errMsg);

        SysUser user = SessionUtil.get();
        if (isFirstCreate) setAddInfo(user, msg);

        msg.setPid(user.getId());
        msg.setPname(user.getName());
        msg.setPtime(System.currentTimeMillis());
        msg.setStatus(MsgConstant.STATUS_PUBLISHED);

        return service.publish(msg);
    }

    @PostMapping("withdraw")
    public R withdraw(@RequestBody Msg msg) {
        if (msg.getId() == null) return R.fail("参数错误");

        UserVo user = SessionUtil.get();

        msg.setWid(user.getId());
        msg.setWname(user.getName());
        msg.setWtime(System.currentTimeMillis());

        return service.withdraw(msg);
    }

    @GetMapping("del")
    public R del(@RequestParam Integer id, @RequestParam String title) {
        return service.del(id, title);
    }

    private void setAddInfo(SysUser user, Msg msg) {
        msg.setId(null);
        msg.setCid(user.getId());
        msg.setCname(user.getName());
        msg.setCtime(System.currentTimeMillis());
        msg.setStatus(MsgConstant.STATUS_DRAFT);
    }

    private String validateAdd(Msg msg) {
        if (StringUtils.isEmpty(msg.getTitle())
                || msg.getType() == null
                || !msg.isBroadcast() && StringUtils.isEmpty(msg.getRecipient())
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

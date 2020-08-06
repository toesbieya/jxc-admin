package cn.toesbieya.jxc.controller.msg;

import cn.toesbieya.jxc.constant.MsgConstant;
import cn.toesbieya.jxc.model.entity.Msg;
import cn.toesbieya.jxc.model.vo.Result;
import cn.toesbieya.jxc.model.vo.UserVo;
import cn.toesbieya.jxc.model.vo.search.MsgSearch;
import cn.toesbieya.jxc.service.msg.MsgService;
import cn.toesbieya.jxc.util.SessionUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("message/manage")
public class ManageController {
    @Resource
    private MsgService service;

    @PostMapping("search")
    public Result search(@RequestBody MsgSearch vo) {
        return Result.success(service.search(vo));
    }

    @PostMapping("add")
    public Result add(@RequestBody Msg msg) {
        String errMsg = validateAdd(msg);
        if (errMsg != null) return Result.fail(errMsg);

        UserVo user = SessionUtil.get();

        setAddInfo(user, msg);

        return service.add(msg);
    }

    @PostMapping("update")
    public Result update(@RequestBody Msg msg) {
        String errMsg = validateAdd(msg);
        if (errMsg == null) errMsg = validateUpdate(msg);
        if (errMsg != null) return Result.fail(errMsg);

        return service.update(msg);
    }

    @PostMapping("publish")
    public Result publish(@RequestBody Msg msg) {
        boolean isFirstCreate = msg.getId() == null;

        String errMsg = validateAdd(msg);
        if (errMsg == null && !isFirstCreate) errMsg = validateUpdate(msg);
        if (errMsg != null) return Result.fail(errMsg);

        UserVo user = SessionUtil.get();

        if (isFirstCreate) setAddInfo(user, msg);

        msg.setPid(user.getId());
        msg.setPname(user.getName());
        msg.setPtime(System.currentTimeMillis());
        msg.setStatus(MsgConstant.STATUS_PUBLISHED);

        return service.publish(msg);
    }

    @PostMapping("withdraw")
    public Result withdraw(@RequestBody Msg msg) {
        if (msg.getId() == null) return Result.fail("参数错误");

        UserVo user = SessionUtil.get();

        msg.setWid(user.getId());
        msg.setWname(user.getName());
        msg.setWtime(System.currentTimeMillis());

        return service.withdraw(msg);
    }

    @GetMapping("del")
    public Result del(@RequestParam Integer id, @RequestParam String title) {
        return service.del(id, title);
    }

    private void setAddInfo(UserVo user, Msg msg) {
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

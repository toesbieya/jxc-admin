package cn.toesbieya.jxc.controller;

import cn.toesbieya.jxc.model.vo.UserVo;
import cn.toesbieya.jxc.model.vo.search.LoginHistorySearch;
import cn.toesbieya.jxc.model.vo.search.UserActionSearch;
import cn.toesbieya.jxc.util.SessionUtil;
import cn.toesbieya.jxc.service.RecService;
import cn.toesbieya.jxc.util.DateUtil;
import cn.toesbieya.jxc.model.vo.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("record")
public class RecController {
    @Resource
    private RecService service;

    @PostMapping("searchLoginHistory")
    public Result searchLoginHistory(@RequestBody LoginHistorySearch vo) {
        UserVo user = SessionUtil.get();

        vo.setUid(user.getId());
        vo.setStartTime(DateUtil.getTimestampBeforeNow(7));

        return Result.success(service.searchLoginHistory(vo));
    }

    @PostMapping("searchUserAction")
    public Result searchUserAction(@RequestBody UserActionSearch vo) {
        UserVo user = SessionUtil.get();

        vo.setUid(user.getId());
        vo.setStartTime(DateUtil.getTimestampBeforeNow(7));

        return Result.success(service.searchUserAction(vo));
    }
}

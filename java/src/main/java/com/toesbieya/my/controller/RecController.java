package com.toesbieya.my.controller;

import com.toesbieya.my.model.vo.UserVo;
import com.toesbieya.my.model.vo.search.LoginHistorySearch;
import com.toesbieya.my.model.vo.search.UserActionSearch;
import com.toesbieya.my.service.RecService;
import com.toesbieya.my.utils.DateUtil;
import com.toesbieya.my.utils.Result;
import com.toesbieya.my.utils.SessionUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("record")
public class RecController {
    @Resource
    private RecService recService;

    @PostMapping("searchLoginHistory")
    public Result searchLoginHistory(@RequestBody LoginHistorySearch vo) {
        UserVo user = SessionUtil.get();

        vo.setUid(user.getId());
        vo.setStartTime(DateUtil.getTimestampBeforeNow(7));

        return Result.success(recService.searchLoginHistory(vo));
    }

    @PostMapping("searchUserAction")
    public Result searchUserAction(@RequestBody UserActionSearch vo) {
        UserVo user = SessionUtil.get();

        vo.setUid(user.getId());
        vo.setStartTime(DateUtil.getTimestampBeforeNow(7));

        return Result.success(recService.searchUserAction(vo));
    }
}

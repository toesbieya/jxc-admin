package com.toesbieya.my.controller;

import com.toesbieya.my.model.entity.SysUser;
import com.toesbieya.my.model.vo.search.LoginHistorySearch;
import com.toesbieya.my.model.vo.search.UserActionSearch;
import com.toesbieya.my.service.RecService;
import com.toesbieya.my.utils.DateUtil;
import com.toesbieya.my.utils.Result;
import com.toesbieya.my.utils.Util;
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
        SysUser user = Util.getUser();
        vo.setUid(user.getId());
        vo.setStartTime(DateUtil.getTimestampBeforeNow(7));

        return Result.success(recService.searchLoginHistory(vo));
    }

    @PostMapping("searchUserAction")
    public Result searchUserAction(@RequestBody UserActionSearch vo) {
        SysUser user = Util.getUser();
        vo.setUid(user.getId());
        vo.setStartTime(DateUtil.getTimestampBeforeNow(7));

        return Result.success(recService.searchUserAction(vo));
    }
}

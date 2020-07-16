package cn.toesbieya.jxc.record.controller;

import cn.toesbieya.jxc.common.model.vo.Result;
import cn.toesbieya.jxc.record.model.vo.LoginHistorySearch;
import cn.toesbieya.jxc.record.model.vo.UserActionSearch;
import cn.toesbieya.jxc.record.service.RecordService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("record")
public class RecordController {
    @Resource
    private RecordService recordService;

    @PostMapping("searchLoginHistory")
    public Result searchLoginHistory(@RequestBody LoginHistorySearch vo) {
        return Result.success(recordService.searchLoginHistory(vo));
    }

    @PostMapping("searchUserAction")
    public Result searchUserAction(@RequestBody UserActionSearch vo) {
        return Result.success(recordService.searchUserAction(vo));
    }
}

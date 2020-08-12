package cn.toesbieya.jxc.record.controller;

import cn.toesbieya.jxc.common.model.vo.R;
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
    private RecordService service;

    @PostMapping("searchLoginHistory")
    public R searchLoginHistory(@RequestBody LoginHistorySearch vo) {
        return R.success(service.searchLoginHistory(vo));
    }

    @PostMapping("searchUserAction")
    public R searchUserAction(@RequestBody UserActionSearch vo) {
        return R.success(service.searchUserAction(vo));
    }
}

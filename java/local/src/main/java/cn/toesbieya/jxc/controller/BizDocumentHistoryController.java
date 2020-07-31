package cn.toesbieya.jxc.controller;

import cn.toesbieya.jxc.model.vo.search.DocumentHistorySearch;
import cn.toesbieya.jxc.service.BizDocumentHistoryService;
import cn.toesbieya.jxc.model.vo.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("document/history")
public class BizDocumentHistoryController {
    @Resource
    private BizDocumentHistoryService service;

    @GetMapping("get")
    public Result get(@RequestParam("pid") String pid) {
        return Result.success(service.getByPid(pid));
    }

    @PostMapping("search")
    public Result search(@RequestBody DocumentHistorySearch vo) {
        return Result.success(service.search(vo));
    }
}

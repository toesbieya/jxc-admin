package cn.toesbieya.jxc.doc.controller;

import cn.toesbieya.jxc.common.model.vo.Result;
import cn.toesbieya.jxc.doc.model.vo.DocHistorySearch;
import cn.toesbieya.jxc.doc.service.DocHistoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("history")
public class DocHistoryController {
    @Resource
    private DocHistoryService service;

    @PostMapping("search")
    public Result search(@RequestBody DocHistorySearch vo) {
        return Result.success(service.search(vo));
    }
}

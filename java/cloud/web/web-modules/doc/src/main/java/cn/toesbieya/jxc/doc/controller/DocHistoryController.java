package cn.toesbieya.jxc.doc.controller;

import cn.toesbieya.jxc.common.model.vo.R;
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
    public R search(@RequestBody DocHistorySearch vo) {
        return R.success(service.search(vo));
    }
}

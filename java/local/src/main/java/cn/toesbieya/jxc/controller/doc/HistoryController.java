package cn.toesbieya.jxc.controller.doc;

import cn.toesbieya.jxc.model.vo.search.DocHistorySearch;
import cn.toesbieya.jxc.service.doc.BizDocHistoryService;
import cn.toesbieya.jxc.model.vo.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("doc/history")
public class HistoryController {
    @Resource
    private BizDocHistoryService service;

    @PostMapping("search")
    public R search(@RequestBody DocHistorySearch vo) {
        return R.success(service.search(vo));
    }
}

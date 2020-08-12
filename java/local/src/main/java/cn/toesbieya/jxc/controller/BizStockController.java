package cn.toesbieya.jxc.controller;

import cn.toesbieya.jxc.model.vo.search.StockSearch;
import cn.toesbieya.jxc.service.BizStockService;
import cn.toesbieya.jxc.model.vo.R;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("stock/current")
public class BizStockController {
    @Resource
    private BizStockService service;

    @PostMapping("search")
    public R search(@RequestBody StockSearch vo) {
        return R.success(service.search(vo));
    }

    @GetMapping("getDetail")
    public R getDetail(@RequestParam String cids) {
        if (StringUtils.isEmpty(cids)) return R.fail("参数错误");
        return R.success(service.getDetail(cids));
    }

    @GetMapping("getDetailById")
    public R getDetailById(@RequestParam String ids) {
        if (StringUtils.isEmpty(ids)) return R.fail("参数错误");
        return R.success(service.getDetailById(ids));
    }

    @PostMapping("export")
    public void export(@RequestBody StockSearch vo, HttpServletResponse response) throws Exception {
        service.export(vo, response);
    }
}

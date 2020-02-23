package com.toesbieya.my.controller;

import com.toesbieya.my.model.vo.search.StockSearch;
import com.toesbieya.my.service.BizStockService;
import com.toesbieya.my.utils.Result;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("stock/current")
public class BizStockController {
    @Resource
    private BizStockService stockService;

    @PostMapping("search")
    public Result search(@RequestBody StockSearch vo) {
        return Result.success(stockService.search(vo));
    }

    @GetMapping("getDetail")
    public Result getDetail(@RequestParam String cid) {
        if (StringUtils.isEmpty(cid)) return Result.fail("参数错误");
        return Result.success(stockService.getDetail(cid));
    }

    @GetMapping("getDetailById")
    public Result getDetailById(@RequestParam String ids) {
        if (StringUtils.isEmpty(ids)) return Result.fail("参数错误");
        return Result.success(stockService.getDetailById(ids));
    }

    @PostMapping("export")
    public void export(@RequestBody StockSearch vo, HttpServletResponse response) throws Exception {
        stockService.export(vo, response);
    }
}

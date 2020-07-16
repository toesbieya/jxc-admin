package cn.toesbieya.jxc.stock.controller;

import cn.toesbieya.jxc.common.model.vo.Result;
import cn.toesbieya.jxc.stock.service.StockService;
import cn.toesbieya.jxc.common.model.vo.StockSearch;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("stock/current")
public class StockController {
    @Resource
    private StockService stockService;

    @PostMapping("search")
    public Result search(@RequestBody StockSearch vo) {
        return Result.success(stockService.search(vo));
    }

    @GetMapping("getDetail")
    public Result getDetail(@RequestParam String cids) {
        if (StringUtils.isEmpty(cids)) return Result.fail("参数错误");
        return Result.success(stockService.getDetail(cids));
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

package cn.toesbieya.jxc.statistic.controller;

import cn.toesbieya.jxc.common.model.vo.Result;
import cn.toesbieya.jxc.statistic.service.StatisticService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("statistic")
public class StatisticController {
    @Resource
    private StatisticService service;

    @GetMapping("getFourBlock")
    public Result getFourBlock() {
        return Result.success(service.getFourBlock());
    }

    @GetMapping("getDailyProfitStat")
    public Result getDailyProfitStat() {
        return Result.success(service.getDailyProfitStat());
    }

    @GetMapping("getDailyFinishOrder")
    public Result getDailyFinishOrder() {
        return Result.success(service.getDailyFinishOrder());
    }

    @GetMapping("getTotalProfitGoods")
    public Result getTotalProfitGoods() {
        return Result.success(service.getTotalProfitGoods());
    }
}

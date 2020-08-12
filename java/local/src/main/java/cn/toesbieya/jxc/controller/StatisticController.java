package cn.toesbieya.jxc.controller;

import cn.toesbieya.jxc.service.StatisticService;
import cn.toesbieya.jxc.model.vo.R;
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
    public R getFourBlock() {
        return R.success(service.getFourBlock());
    }

    @GetMapping("getDailyProfitStat")
    public R getDailyProfitStat() {
        return R.success(service.getDailyProfitStat());
    }

    @GetMapping("getDailyFinishOrder")
    public R getDailyFinishOrder() {
        return R.success(service.getDailyFinishOrder());
    }

    @GetMapping("getTotalProfitGoods")
    public R getTotalProfitGoods() {
        return R.success(service.getTotalProfitGoods());
    }
}

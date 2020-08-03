package cn.toesbieya.jxc.statistic.service;

import cn.toesbieya.jxc.common.model.entity.StatFinishOrder;
import cn.toesbieya.jxc.common.model.entity.StatProfitGoods;
import cn.toesbieya.jxc.common.model.entity.StatProfitTotal;
import cn.toesbieya.jxc.common.util.DateUtil;
import cn.toesbieya.jxc.common.util.WebSocketUtil;
import cn.toesbieya.jxc.statistic.mapper.StatisticMapper;
import cn.toesbieya.jxc.statistic.model.vo.FourBlockStat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StatisticService {
    @Resource
    private StatisticMapper mapper;

    //获取首页四个色块的数据，在线用户、今日采购额、今日销售额、今日毛利润
    public FourBlockStat getFourBlock() {
        long now = DateUtil.getTimestampNow();

        FourBlockStat stat = new FourBlockStat();

        BigDecimal purchase = mapper.getPurchaseOrderDailyTotalPurchasePrice(now, null);
        BigDecimal sell = mapper.getSellOrderDailyTotalPurchasePrice(now, null);

        long onlineNum = WebSocketUtil.getOnlineUserNum();

        stat.setOnline(onlineNum == 0L ? 1L : onlineNum);//进行此请求时在线用户必然大于0
        stat.setPurchase(purchase);
        stat.setSell(sell);
        stat.setProfit(sell.subtract(purchase));

        return stat;
    }

    //获取历史七天的每日采购额、销售额、毛利润
    public List<StatProfitTotal> getDailyProfitStat() {
        long lastWeekDay = DateUtil.getTimestampBeforeNow(7);
        return mapper.searchTotalProfit(lastWeekDay, null);
    }

    //获取历史七天已完成的采购订单数、销售订单数
    public List<StatFinishOrder> getDailyFinishOrder() {
        long lastWeekDay = DateUtil.getTimestampBeforeNow(7);
        return mapper.searchFinishOrder(lastWeekDay, null);
    }

    //获取每种商品的采购总额、销售总额、总毛利
    public Collection<StatProfitGoods> getTotalProfitGoods() {
        //获取历史的信息
        List<StatProfitGoods> history = mapper.getHistoryTotalProfitGoods();

        //获取今日的信息
        long today = DateUtil.getTimestampNow();
        long nextDay = DateUtil.getTimestampBeforeNow(-1);
        history.addAll(mapper.getPurchaseOrderDailyProfitGoods(today, nextDay));
        history.addAll(mapper.getSellOrderDailyProfitGoods(today, nextDay));

        Map<Integer, StatProfitGoods> map = new HashMap<>(history.size());

        //根据商品分类计算
        for (StatProfitGoods p : history) {
            Integer cid = p.getCid();

            if (p.getPurchase() == null) {
                p.setPurchase(new BigDecimal(0));
            }
            if (p.getSell() == null) {
                p.setSell(new BigDecimal(0));
            }
            if (p.getProfit() == null) {
                p.setProfit(p.getSell().subtract(p.getPurchase()));
            }

            StatProfitGoods already = map.get(cid);

            if (already == null) map.put(cid, p);
            else {
                BigDecimal pT = already.getPurchase().add(p.getPurchase());
                BigDecimal sT = already.getSell().add(p.getSell());
                already.setPurchase(pT);
                already.setSell(sT);
                already.setProfit(sT.subtract(pT));
            }
        }

        return map.values();
    }
}

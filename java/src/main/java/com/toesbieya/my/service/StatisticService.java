package com.toesbieya.my.service;

import com.toesbieya.my.mapper.BizPurchaseOrderMapper;
import com.toesbieya.my.mapper.BizSellOrderMapper;
import com.toesbieya.my.mapper.StatisticMapper;
import com.toesbieya.my.model.entity.StatFinishOrder;
import com.toesbieya.my.model.entity.StatProfitGoods;
import com.toesbieya.my.model.entity.StatProfitTotal;
import com.toesbieya.my.model.vo.statictis.FourBlockStat;
import com.toesbieya.my.module.SocketModule;
import com.toesbieya.my.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StatisticService {
    @Resource
    private BizPurchaseOrderMapper purchaseOrderMapper;
    @Resource
    private BizSellOrderMapper sellOrderMapper;
    @Resource
    private StatisticMapper statisticMapper;

    //获取首页四个色块的数据，在线用户、今日采购额、今日销售额、今日毛利润
    public FourBlockStat getFourBlock() {
        long now = DateUtil.getTimestampNow();
        FourBlockStat stat = new FourBlockStat();
        double purchase = purchaseOrderMapper.getDailyTotalPurchasePrice(now, null);
        double sell = sellOrderMapper.getDailyTotalPurchasePrice(now, null);
        int onlineNum = SocketModule.getOnlineNum();
        stat.setOnline(onlineNum == 0 ? 1 : onlineNum);//进行此请求时在线用户必然大于0
        stat.setPurchase(purchase);
        stat.setSell(sell);
        stat.setProfit(sell - purchase);
        return stat;
    }

    //获取历史七天的每日采购额、销售额、毛利润
    public List<StatProfitTotal> getDailyProfitStat() {
        long lastWeekDay = DateUtil.getTimestampBeforeNow(7);
        return statisticMapper.searchTotalProfit(lastWeekDay, null);
    }

    //获取历史七天已完成的采购订单数、销售订单数
    public List<StatFinishOrder> getDailyFinishOrder() {
        long lastWeekDay = DateUtil.getTimestampBeforeNow(7);
        return statisticMapper.searchFinishOrder(lastWeekDay, null);
    }

    //获取每种商品的采购总额、销售总额、总毛利
    public Collection<StatProfitGoods> getTotalProfitGoods() {
        //获取历史的信息
        List<StatProfitGoods> history = statisticMapper.getHistoryTotalProfitGoods();

        //获取今日的信息
        long today = DateUtil.getTimestampNow();
        long nextDay = DateUtil.getTimestampBeforeNow(-1);
        history.addAll(purchaseOrderMapper.getDailyProfitGoods(today, nextDay));
        history.addAll(sellOrderMapper.getDailyProfitGoods(today, nextDay));

        Map<Integer, StatProfitGoods> map = new HashMap<>(history.size());

        for (StatProfitGoods p : history) {
            Integer cid = p.getCid();
            if (p.getPurchase() == null) p.setPurchase(0D);
            if (p.getSell() == null) p.setSell(0D);
            if (p.getProfit() == null) p.setProfit(p.getSell() - p.getPurchase());
            StatProfitGoods already = map.get(cid);
            if (already == null) map.put(cid, p);
            else {
                Double pT = already.getPurchase() + p.getPurchase();
                Double sT = already.getSell() + p.getSell();
                already.setPurchase(pT);
                already.setSell(sT);
                already.setProfit(sT - pT);
            }
        }

        return map.values();
    }

    //每天零点统计信息
    @Async("scheduledExecutor")
    @Scheduled(cron = "1 0 0 */1 * ?")
    @PostConstruct
    public void autoStat() {
        log.info("开始统计信息...");

        long lastDay = DateUtil.getTimestampBeforeNow(1);
        long today = DateUtil.getTimestampNow();

        //检查是否已统计过昨日已完成的订单数
        if (!statisticMapper.checkDailyFinishOrderExist(lastDay)) {
            Integer lastDayPurchaseOrderFinish = purchaseOrderMapper.getLastDayFinishOrderNum();
            Integer lastDaySellOrderFinish = sellOrderMapper.getLastDayFinishOrderNum();
            StatFinishOrder param = new StatFinishOrder();
            param.setPurchase(lastDayPurchaseOrderFinish);
            param.setSell(lastDaySellOrderFinish);
            param.setTime(lastDay);
            statisticMapper.insertFinishOrder(param);
        }

        //检查是否已统计过昨日的采购额、销售额、毛利
        if (!statisticMapper.checkDailyProfitExist(lastDay)) {
            //昨日各个商品的采购额、销售额
            List<StatProfitGoods> purchaseProfitGoods = purchaseOrderMapper.getDailyProfitGoods(lastDay, today);
            List<StatProfitGoods> sellProfitGoods = sellOrderMapper.getDailyProfitGoods(lastDay, today);

            //昨日的采购总额、销售总额
            double totalPurchase = 0;
            double totalSell = 0;

            Map<Integer, StatProfitGoods> map = new HashMap<>(sellProfitGoods.size());
            for (StatProfitGoods p : sellProfitGoods) {
                map.put(p.getCid(), p);
            }

            for (StatProfitGoods p : purchaseProfitGoods) {
                p.setTime(lastDay);
                Integer cid = p.getCid();
                Double purchase = p.getPurchase();
                totalPurchase += purchase;
                StatProfitGoods s = map.get(cid);
                if (s != null) {
                    Double sell = s.getSell();
                    totalSell += sell;
                    p.setSell(sell);
                    p.setProfit(sell - purchase);
                    map.remove(cid);
                }
                else {
                    p.setSell(0D);
                    p.setProfit(-purchase);
                }
            }

            for (StatProfitGoods s : map.values()) {
                s.setTime(lastDay);
                s.setPurchase(0D);
                Double sell = s.getSell();
                s.setProfit(sell);
                totalSell += sell;
                purchaseProfitGoods.add(s);
            }

            double totalProfit = totalSell - totalPurchase;

            StatProfitTotal statProfitTotal = new StatProfitTotal();
            statProfitTotal.setPurchase(totalPurchase);
            statProfitTotal.setSell(totalSell);
            statProfitTotal.setProfit(totalProfit);
            statProfitTotal.setTime(lastDay);

            statisticMapper.insertProfitTotal(statProfitTotal);

            if (purchaseProfitGoods.size() > 0) {
                statisticMapper.insertProfitGoodsBatch(purchaseProfitGoods);
            }
        }

        log.info("结束统计信息...");
    }
}

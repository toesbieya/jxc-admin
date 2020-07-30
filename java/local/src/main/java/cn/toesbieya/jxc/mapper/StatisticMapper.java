package cn.toesbieya.jxc.mapper;

import cn.toesbieya.jxc.model.entity.StatFinishOrder;
import cn.toesbieya.jxc.model.entity.StatProfitGoods;
import cn.toesbieya.jxc.model.entity.StatProfitTotal;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface StatisticMapper {
    List<StatProfitTotal> searchTotalProfit(@Param("start") Long start, @Param("end") Long end);

    List<StatProfitGoods> searchGoodsProfit(@Param("start") Long start, @Param("end") Long end);

    List<StatFinishOrder> searchFinishOrder(@Param("start") Long start, @Param("end") Long end);

    List<StatProfitGoods> getHistoryTotalProfitGoods();

    boolean checkDailyFinishOrderExist(@Param("time") long time);

    boolean checkDailyProfitExist(@Param("time") long time);

    int insertFinishOrder(StatFinishOrder param);

    int insertProfitTotal(StatProfitTotal param);

    int insertProfitGoodsBatch(@Param("list") List<StatProfitGoods> list);

    BigDecimal getPurchaseOrderDailyTotalPurchasePrice(@Param("start") Long start, @Param("end") Long end);

    List<StatProfitGoods> getPurchaseOrderDailyProfitGoods(@Param("start") long start, @Param("end") long end);

    Integer getPurchaseOrderLastDayFinishOrderNum();

    BigDecimal getSellOrderDailyTotalPurchasePrice(@Param("start") Long start, @Param("end") Long end);

    List<StatProfitGoods> getSellOrderDailyProfitGoods(@Param("start") long start, @Param("end") long end);

    Integer getSellOrderLastDayFinishOrderNum();
}

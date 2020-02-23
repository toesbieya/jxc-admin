package com.toesbieya.my.mapper;

import com.toesbieya.my.model.entity.StatFinishOrder;
import com.toesbieya.my.model.entity.StatProfitGoods;
import com.toesbieya.my.model.entity.StatProfitTotal;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StatisticMapper {
    List<StatProfitTotal> searchTotalProfit(@Param("start") Long start, @Param("end") Long end);

    List<StatProfitGoods> searchGoodsProfit(@Param("start") Long start, @Param("end") Long end);

    List<StatFinishOrder> searchFinishOrder(@Param("start") Long start, @Param("end") Long end);

    List<StatProfitGoods> getHistoryTotalProfitGoods();

    boolean checkDailyFinishOrderExist(@Param("time") long time);

    int insertFinishOrder(StatFinishOrder param);

    boolean checkDailyProfitExist(@Param("time") long time);

    int insertProfitTotal(StatProfitTotal param);

    int insertProfitGoodsBatch(@Param("list") List<StatProfitGoods> list);
}

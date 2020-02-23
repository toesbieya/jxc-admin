package com.toesbieya.my.mapper;

import com.toesbieya.my.model.entity.BizSellOrder;
import com.toesbieya.my.model.entity.BizSellOrderSub;
import com.toesbieya.my.model.entity.StatProfitGoods;
import com.toesbieya.my.model.vo.export.SellOrderExport;
import com.toesbieya.my.model.vo.search.SellOrderSearch;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BizSellOrderMapper {
    BizSellOrder getById(@Param("id") String id);

    List<BizSellOrderSub> getSubById(@Param("id") String id);

    List<BizSellOrder> search(SellOrderSearch vo);

    List<SellOrderExport> export(SellOrderSearch vo);

    int add(BizSellOrder param);

    void addSub(List<BizSellOrderSub> list);

    int update(BizSellOrder param);

    int updateSubRemainNum(@Param("id") Integer id, @Param("remain_num") double remain_num);

    int pass(@Param("id") String id, @Param("vid") Integer vid, @Param("vname") String vname, @Param("vtime") long vtime);

    int reject(@Param("id") String id);

    int del(@Param("id") String id);

    void delSubByPid(@Param("pid") String pid);

    int updateFinish(@Param("id") String id, @Param("finish") Integer finish, @Param("ftime") Long ftime);

    double getDailyTotalPurchasePrice(@Param("start") Long start, @Param("end") Long end);

    List<StatProfitGoods> getDailyProfitGoods(@Param("start") long start, @Param("end") long end);

    Integer getLastDayFinishOrderNum();
}

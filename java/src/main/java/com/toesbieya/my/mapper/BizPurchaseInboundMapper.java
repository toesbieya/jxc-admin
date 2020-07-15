package com.toesbieya.my.mapper;

import com.toesbieya.my.model.entity.BizPurchaseInbound;
import com.toesbieya.my.model.entity.BizPurchaseInboundSub;
import com.toesbieya.my.model.vo.export.PurchaseInboundExport;
import com.toesbieya.my.model.vo.search.PurchaseInboundSearch;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BizPurchaseInboundMapper {
    BizPurchaseInbound getById(@Param("id") String id);

    List<BizPurchaseInboundSub> getSubById(@Param("id") String id);

    List<BizPurchaseInbound> search(PurchaseInboundSearch vo);

    List<PurchaseInboundExport> export(PurchaseInboundSearch vo);

    int insert(BizPurchaseInbound param);

    void addSub(List<BizPurchaseInboundSub> list);

    int update(BizPurchaseInbound param);

    int pass(@Param("id") String id, @Param("vid") Integer vid, @Param("vname") String vname, @Param("vtime") long vtime);

    int reject(@Param("id") String id);

    int del(@Param("id") String id);

    void delSubByPid(@Param("pid") String pid);
}

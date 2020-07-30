package cn.toesbieya.jxc.mapper;

import cn.toesbieya.jxc.model.entity.BizPurchaseInbound;
import cn.toesbieya.jxc.model.entity.BizPurchaseInboundSub;
import cn.toesbieya.jxc.model.vo.search.PurchaseInboundSearch;
import cn.toesbieya.jxc.model.vo.export.PurchaseInboundExport;
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

package cn.toesbieya.jxc.mapper;

import cn.toesbieya.jxc.model.entity.BizSellOutboundSub;
import cn.toesbieya.jxc.model.entity.BizSellOutbound;
import cn.toesbieya.jxc.model.vo.export.SellOutboundExport;
import cn.toesbieya.jxc.model.vo.search.SellOutboundSearch;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BizSellOutboundMapper {
    BizSellOutbound getById(@Param("id") String id);

    List<BizSellOutboundSub> getSubById(@Param("id") String id);

    List<BizSellOutbound> search(SellOutboundSearch vo);

    List<SellOutboundExport> export(SellOutboundSearch vo);

    int insert(BizSellOutbound param);

    void addSub(List<BizSellOutboundSub> list);

    int update(BizSellOutbound param);

    int pass(@Param("id") String id, @Param("vid") Integer vid, @Param("vname") String vname, @Param("vtime") long vtime);

    int reject(@Param("id") String id);

    int del(@Param("id") String id);

    void delSubByPid(@Param("pid") String pid);
}

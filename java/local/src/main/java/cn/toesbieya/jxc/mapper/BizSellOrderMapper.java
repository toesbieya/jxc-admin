package cn.toesbieya.jxc.mapper;

import cn.toesbieya.jxc.model.entity.BizSellOrder;
import cn.toesbieya.jxc.model.entity.BizSellOrderSub;
import cn.toesbieya.jxc.model.vo.export.SellOrderExport;
import cn.toesbieya.jxc.model.vo.search.SellOrderSearch;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface BizSellOrderMapper {
    BizSellOrder getById(@Param("id") String id);

    List<BizSellOrderSub> getSubById(@Param("id") String id);

    List<BizSellOrder> search(SellOrderSearch vo);

    List<SellOrderExport> export(SellOrderSearch vo);

    int insert(BizSellOrder param);

    void addSub(List<BizSellOrderSub> list);

    int update(BizSellOrder param);

    int updateSubRemainNum(@Param("id") Integer id, @Param("remain_num") BigDecimal remain_num);

    int pass(@Param("id") String id, @Param("vid") Integer vid, @Param("vname") String vname, @Param("vtime") long vtime);

    int reject(@Param("id") String id);

    int del(@Param("id") String id);

    void delSubByPid(@Param("pid") String pid);

    int updateFinish(@Param("id") String id, @Param("finish") Integer finish, @Param("ftime") Long ftime);
}

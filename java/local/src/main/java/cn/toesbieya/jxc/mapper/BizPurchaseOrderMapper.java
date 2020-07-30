package cn.toesbieya.jxc.mapper;

import cn.toesbieya.jxc.model.entity.BizPurchaseOrder;
import cn.toesbieya.jxc.model.entity.BizPurchaseOrderSub;
import cn.toesbieya.jxc.model.vo.export.PurchaseOrderExport;
import cn.toesbieya.jxc.model.vo.search.PurchaseOrderSearch;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface BizPurchaseOrderMapper {
    BizPurchaseOrder getById(@Param("id") String id);

    List<BizPurchaseOrderSub> getSubById(@Param("id") String id);

    List<BizPurchaseOrder> search(PurchaseOrderSearch vo);

    List<PurchaseOrderExport> export(PurchaseOrderSearch vo);

    int insert(BizPurchaseOrder param);

    void addSub(List<BizPurchaseOrderSub> list);

    int update(BizPurchaseOrder param);

    int updateSubRemainNum(@Param("id") Integer id, @Param("remain_num") BigDecimal remain_num);

    int pass(@Param("id") String id, @Param("vid") Integer vid, @Param("vname") String vname, @Param("vtime") long vtime);

    int reject(@Param("id") String id);

    int del(@Param("id") String id);

    void delSubByPid(@Param("pid") String pid);

    int updateFinish(@Param("id") String id, @Param("finish") Integer finish, @Param("ftime") Long ftime);
}

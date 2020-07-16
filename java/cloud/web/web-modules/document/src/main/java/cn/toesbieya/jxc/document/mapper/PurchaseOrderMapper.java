package cn.toesbieya.jxc.document.mapper;

import cn.toesbieya.jxc.common.model.entity.BizPurchaseOrder;
import cn.toesbieya.jxc.document.model.vo.PurchaseOrderExport;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PurchaseOrderMapper extends BaseMapper<BizPurchaseOrder> {
    List<PurchaseOrderExport> export(@Param(Constants.WRAPPER) Wrapper<BizPurchaseOrder> wrapper);
}

package cn.toesbieya.jxc.mapper;

import cn.toesbieya.jxc.model.entity.BizPurchaseOrder;
import cn.toesbieya.jxc.model.vo.export.PurchaseOrderExport;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BizPurchaseOrderMapper extends BaseMapper<BizPurchaseOrder> {
    List<PurchaseOrderExport> export(@Param(Constants.WRAPPER) Wrapper<BizPurchaseOrder> wrapper);
}

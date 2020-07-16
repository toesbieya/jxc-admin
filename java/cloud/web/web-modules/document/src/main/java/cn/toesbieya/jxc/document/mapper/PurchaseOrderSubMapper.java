package cn.toesbieya.jxc.document.mapper;

import cn.toesbieya.jxc.common.model.entity.BizPurchaseOrderSub;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface PurchaseOrderSubMapper extends BaseMapper<BizPurchaseOrderSub> {
    void insertBatch(List<BizPurchaseOrderSub> list);
}

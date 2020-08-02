package cn.toesbieya.jxc.mapper;

import cn.toesbieya.jxc.model.entity.BizPurchaseOrderSub;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface BizPurchaseOrderSubMapper extends BaseMapper<BizPurchaseOrderSub> {
    void insertBatch(List<BizPurchaseOrderSub> list);
}

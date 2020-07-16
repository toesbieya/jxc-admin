package cn.toesbieya.jxc.document.mapper;

import cn.toesbieya.jxc.common.model.entity.BizPurchaseInboundSub;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface PurchaseInboundSubMapper extends BaseMapper<BizPurchaseInboundSub> {
    void insertBatch(List<BizPurchaseInboundSub> list);
}

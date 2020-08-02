package cn.toesbieya.jxc.mapper;

import cn.toesbieya.jxc.model.entity.BizPurchaseInboundSub;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface BizPurchaseInboundSubMapper extends BaseMapper<BizPurchaseInboundSub> {
    void insertBatch(List<BizPurchaseInboundSub> list);
}

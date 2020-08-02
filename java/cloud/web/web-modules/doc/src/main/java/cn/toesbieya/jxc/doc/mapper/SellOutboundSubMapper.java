package cn.toesbieya.jxc.doc.mapper;

import cn.toesbieya.jxc.common.model.entity.BizSellOutboundSub;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface SellOutboundSubMapper extends BaseMapper<BizSellOutboundSub> {
    void insertBatch(List<BizSellOutboundSub> list);
}

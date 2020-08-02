package cn.toesbieya.jxc.mapper;

import cn.toesbieya.jxc.model.entity.BizSellOutboundSub;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface BizSellOutboundSubMapper extends BaseMapper<BizSellOutboundSub> {
    void insertBatch(List<BizSellOutboundSub> list);
}

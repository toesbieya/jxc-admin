package cn.toesbieya.jxc.document.mapper;

import cn.toesbieya.jxc.common.model.entity.BizSellOrderSub;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface SellOrderSubMapper extends BaseMapper<BizSellOrderSub> {
    void insertBatch(List<BizSellOrderSub> list);
}

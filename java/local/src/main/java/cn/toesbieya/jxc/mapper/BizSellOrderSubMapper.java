package cn.toesbieya.jxc.mapper;

import cn.toesbieya.jxc.model.entity.BizSellOrderSub;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface BizSellOrderSubMapper extends BaseMapper<BizSellOrderSub> {
    void insertBatch(List<BizSellOrderSub> list);
}

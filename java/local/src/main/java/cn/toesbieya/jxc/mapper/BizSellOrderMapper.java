package cn.toesbieya.jxc.mapper;

import cn.toesbieya.jxc.model.entity.BizSellOrder;
import cn.toesbieya.jxc.model.vo.export.SellOrderExport;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BizSellOrderMapper extends BaseMapper<BizSellOrder> {
    List<SellOrderExport> export(@Param(Constants.WRAPPER) Wrapper<BizSellOrder> wrapper);
}

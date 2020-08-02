package cn.toesbieya.jxc.mapper;

import cn.toesbieya.jxc.model.entity.BizSellOutbound;
import cn.toesbieya.jxc.model.vo.export.SellOutboundExport;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BizSellOutboundMapper extends BaseMapper<BizSellOutbound> {
    List<SellOutboundExport> export(@Param(Constants.WRAPPER) Wrapper<BizSellOutbound> wrapper);
}

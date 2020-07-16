package cn.toesbieya.jxc.document.mapper;

import cn.toesbieya.jxc.common.model.entity.BizSellOutbound;
import cn.toesbieya.jxc.document.model.vo.SellOutboundExport;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SellOutboundMapper extends BaseMapper<BizSellOutbound> {
    List<SellOutboundExport> export(@Param(Constants.WRAPPER) Wrapper<BizSellOutbound> wrapper);
}

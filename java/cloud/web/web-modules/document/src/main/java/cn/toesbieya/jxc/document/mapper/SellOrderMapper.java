package cn.toesbieya.jxc.document.mapper;

import cn.toesbieya.jxc.common.model.entity.BizSellOrder;
import cn.toesbieya.jxc.document.model.vo.SellOrderExport;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SellOrderMapper extends BaseMapper<BizSellOrder> {
    List<SellOrderExport> export(@Param(Constants.WRAPPER) Wrapper<BizSellOrder> wrapper);
}

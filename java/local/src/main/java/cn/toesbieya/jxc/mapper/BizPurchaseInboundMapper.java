package cn.toesbieya.jxc.mapper;

import cn.toesbieya.jxc.model.entity.BizPurchaseInbound;
import cn.toesbieya.jxc.model.vo.export.PurchaseInboundExport;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BizPurchaseInboundMapper extends BaseMapper<BizPurchaseInbound> {
    List<PurchaseInboundExport> export(@Param(Constants.WRAPPER) Wrapper<BizPurchaseInbound> wrapper);
}

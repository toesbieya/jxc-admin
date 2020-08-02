package cn.toesbieya.jxc.doc.mapper;

import cn.toesbieya.jxc.common.model.entity.BizPurchaseInbound;
import cn.toesbieya.jxc.doc.model.vo.PurchaseInboundExport;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PurchaseInboundMapper extends BaseMapper<BizPurchaseInbound> {
    List<PurchaseInboundExport> export(@Param(Constants.WRAPPER) Wrapper<BizPurchaseInbound> wrapper);
}

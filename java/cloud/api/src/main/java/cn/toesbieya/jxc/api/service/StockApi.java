package cn.toesbieya.jxc.api.service;

import cn.toesbieya.jxc.common.model.entity.BizStock;
import cn.toesbieya.jxc.common.model.vo.StockOutboundVo;
import cn.toesbieya.jxc.common.model.vo.StockSearch;
import cn.toesbieya.jxc.common.model.vo.StockSearchResult;

import java.util.List;

public interface StockApi {
    List<StockSearchResult> getByCondition(StockSearch vo);

    List<BizStock> getDetail(String cids);

    void inbound(List<BizStock> list);

    void outbound(List<StockOutboundVo> list);
}

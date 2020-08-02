package cn.toesbieya.jxc.stock.service;

import cn.toesbieya.jxc.api.service.StockApi;
import cn.toesbieya.jxc.common.exception.JsonResultException;
import cn.toesbieya.jxc.common.model.entity.BizStock;
import cn.toesbieya.jxc.common.model.vo.StockOutboundVo;
import cn.toesbieya.jxc.common.model.vo.StockSearch;
import cn.toesbieya.jxc.common.model.vo.StockSearchResult;
import cn.toesbieya.jxc.stock.mapper.StockMapper;
import cn.toesbieya.jxc.stock.model.vo.StockExport;
import cn.toesbieya.jxc.web.common.model.vo.PageResult;
import cn.toesbieya.jxc.web.common.utils.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@Service
@org.apache.dubbo.config.annotation.Service
public class StockService implements StockApi {
    @Resource
    private StockMapper mapper;

    private final ExcelUtil.CommonMergeOptions mergeOptions =
            new ExcelUtil.CommonMergeOptions(
                    new String[]{"id", "cname", "totalNum", "totalPrice", "cgddid", "cgPrice", "cgNum"},
                    "cid",
                    "id"
            );

    public PageResult<StockSearchResult> search(StockSearch vo) {
        PageHelper.startPage(vo.getPage(), vo.getPageSize());
        return new PageResult<>(getByCondition(vo));
    }

    @Override
    public List<StockSearchResult> getByCondition(StockSearch vo) {
        return mapper.search(getSearchCondition(vo));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<BizStock> getDetail(String cids) {
        return mapper.selectList(
                Wrappers.lambdaQuery(BizStock.class)
                        .inSql(!StringUtils.isEmpty(cids), BizStock::getCid, cids)
                        .gt(BizStock::getNum, 0)
                        .orderByDesc(BizStock::getCgddid, BizStock::getCgrkid)
        );
    }

    public List<BizStock> getDetailById(String ids) {
        return mapper.selectList(
                Wrappers.lambdaQuery(BizStock.class)
                        .inSql(!StringUtils.isEmpty(ids), BizStock::getId, ids)
        );
    }

    public void export(StockSearch vo, HttpServletResponse response) throws Exception {
        List<StockExport> list = mapper.export(getSearchCondition(vo));
        ExcelUtil.export(list, response, "库存导出", mergeOptions);
    }

    @Override
    public void inbound(List<BizStock> list) {
        if (CollectionUtils.isEmpty(list)) {
            throw new JsonResultException("入库失败，没有需要入库的商品");
        }
        mapper.insertBatch(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void outbound(List<StockOutboundVo> list) {
        if (CollectionUtils.isEmpty(list)) {
            throw new JsonResultException("出库失败，没有需要出库的商品");
        }
        for (StockOutboundVo vo : list) {
            int rows = mapper.outbound(vo.getId(), vo.getNum());
            if (rows < 1) {
                throw new JsonResultException("出库失败");
            }
        }
    }

    private Wrapper<BizStock> getSearchCondition(StockSearch vo) {
        String ids = vo.getIds();
        String cids = vo.getCids();
        String cgddids = vo.getCgddids();
        String cgrkids = vo.getCgrkids();
        Long startTime = vo.getStartTime();
        Long endTime = vo.getEndTime();

        return Wrappers.lambdaQuery(BizStock.class)
                .inSql(!StringUtils.isEmpty(ids), BizStock::getId, ids)
                .inSql(!StringUtils.isEmpty(cids), BizStock::getCid, cids)
                .inSql(!StringUtils.isEmpty(cgddids), BizStock::getCgddid, cgddids)
                .inSql(!StringUtils.isEmpty(cgrkids), BizStock::getCgrkid, cgrkids)
                .gt(BizStock::getNum, 0)
                .ge(startTime != null, BizStock::getCtime, startTime)
                .le(endTime != null, BizStock::getCtime, endTime);
    }
}

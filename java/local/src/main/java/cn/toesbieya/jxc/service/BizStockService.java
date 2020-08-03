package cn.toesbieya.jxc.service;

import cn.toesbieya.jxc.mapper.BizStockMapper;
import cn.toesbieya.jxc.model.entity.BizStock;
import cn.toesbieya.jxc.model.vo.export.StockExport;
import cn.toesbieya.jxc.model.vo.result.PageResult;
import cn.toesbieya.jxc.model.vo.result.StockSearchResult;
import cn.toesbieya.jxc.model.vo.search.StockSearch;
import cn.toesbieya.jxc.util.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
@Slf4j
public class BizStockService {
    @Resource
    private BizStockMapper mapper;

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

    public List<StockSearchResult> getByCondition(StockSearch vo) {
        return mapper.search(getSearchCondition(vo));
    }

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

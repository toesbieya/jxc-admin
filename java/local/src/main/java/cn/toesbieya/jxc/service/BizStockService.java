package cn.toesbieya.jxc.service;

import cn.toesbieya.jxc.model.entity.BizStock;
import cn.toesbieya.jxc.model.vo.export.StockExport;
import cn.toesbieya.jxc.model.vo.result.PageResult;
import cn.toesbieya.jxc.model.vo.result.StockSearchResult;
import cn.toesbieya.jxc.model.vo.search.StockSearch;
import cn.toesbieya.jxc.utils.ExcelUtil;
import com.github.pagehelper.PageHelper;
import cn.toesbieya.jxc.mapper.BizStockMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
@Slf4j
public class BizStockService {
    @Resource
    private BizStockMapper stockMapper;

    private final ExcelUtil.CommonMergeOptions mergeOptions =
            new ExcelUtil.CommonMergeOptions(
                    new String[]{"id", "cname", "total_num", "total_price", "cgddid", "cg_price", "cg_num"}, "cid", "id");

    public PageResult<StockSearchResult> search(StockSearch vo) {
        PageHelper.startPage(vo.getPage(), vo.getPageSize());
        return new PageResult<>(stockMapper.search(vo));
    }

    public List<BizStock> getDetail(String cids) {
        return stockMapper.getDetail(cids);
    }

    public List<BizStock> getDetailById(String ids) {
        return stockMapper.getDetailById(ids);
    }

    public void export(StockSearch vo, HttpServletResponse response) throws Exception {
        List<StockExport> list = stockMapper.export(vo);
        ExcelUtil.export(list, response, "库存导出", mergeOptions);
    }
}

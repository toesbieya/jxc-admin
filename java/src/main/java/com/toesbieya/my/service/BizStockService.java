package com.toesbieya.my.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageSerializable;
import com.toesbieya.my.mapper.BizStockMapper;
import com.toesbieya.my.model.entity.BizStock;
import com.toesbieya.my.model.vo.export.StockExport;
import com.toesbieya.my.model.vo.result.StockSearchResult;
import com.toesbieya.my.model.vo.search.StockSearch;
import com.toesbieya.my.utils.ExcelUtil;
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

    private ExcelUtil.CommonMergeOptions mergeOptions =
            new ExcelUtil.CommonMergeOptions(
                    new String[]{"id", "cname", "total_num", "total_price", "cgddid", "cg_price", "cg_num"}, "cid", "id");

    public PageSerializable<StockSearchResult> search(StockSearch vo) {
        PageHelper.startPage(vo.getPage(), vo.getPageSize());
        return new PageSerializable<>(stockMapper.search(vo));
    }

    public List<BizStock> getDetail(String cid) {
        return stockMapper.getDetail(cid);
    }

    public List<BizStock> getDetailById(String ids) {
        return stockMapper.getDetailById(ids);
    }

    public void export(StockSearch vo, HttpServletResponse response) throws Exception {
        List<StockExport> list = stockMapper.export(vo);
        ExcelUtil.exportWithMerge(list, response, "库存导出", mergeOptions);
    }
}

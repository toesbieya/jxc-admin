package com.toesbieya.my.mapper;

import com.toesbieya.my.model.entity.BizStock;
import com.toesbieya.my.model.vo.export.StockExport;
import com.toesbieya.my.model.vo.result.StockSearchResult;
import com.toesbieya.my.model.vo.search.StockSearch;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface BizStockMapper {
    List<StockSearchResult> search(StockSearch vo);

    List<BizStock> getDetail(@Param("cids") String cids);

    List<BizStock> getDetailById(@Param("ids") String ids);

    List<StockExport> export(StockSearch vo);

    int batchInsert(@Param("list") List<BizStock> list);

    int outbound(@Param("id") Integer id, @Param("num") BigDecimal num);
}
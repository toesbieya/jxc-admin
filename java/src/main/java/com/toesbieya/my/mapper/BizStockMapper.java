package com.toesbieya.my.mapper;

import com.toesbieya.my.model.entity.BizStock;
import com.toesbieya.my.model.vo.export.StockExport;
import com.toesbieya.my.model.vo.result.StockSearchResult;
import com.toesbieya.my.model.vo.search.StockSearch;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BizStockMapper {
    List<StockSearchResult> search(StockSearch vo);

    List<BizStock> getDetail(@Param("cid") String cid);

    List<BizStock> getDetailById(@Param("ids") String ids);

    List<StockExport> export(StockSearch vo);

    int deleteByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BizStock record);

    int updateByPrimaryKey(BizStock record);

    int updateBatchSelective(List<BizStock> list);

    int batchInsert(@Param("list") List<BizStock> list);

    int outbound(@Param("id") Integer id, @Param("num") Double num);
}
package cn.toesbieya.jxc.mapper;

import cn.toesbieya.jxc.model.entity.BizStock;
import cn.toesbieya.jxc.model.vo.export.StockExport;
import cn.toesbieya.jxc.model.vo.result.StockSearchResult;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface BizStockMapper extends BaseMapper<BizStock> {
    List<StockSearchResult> search(@Param(Constants.WRAPPER) Wrapper<BizStock> wrapper);

    List<StockExport> export(@Param(Constants.WRAPPER) Wrapper<BizStock> wrapper);

    int insertBatch(@Param("list") List<BizStock> list);

    int outbound(@Param("id") Integer id, @Param("num") BigDecimal num);
}
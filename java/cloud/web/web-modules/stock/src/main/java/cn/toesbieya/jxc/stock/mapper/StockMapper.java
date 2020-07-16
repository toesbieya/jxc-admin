package cn.toesbieya.jxc.stock.mapper;

import cn.toesbieya.jxc.common.model.entity.BizStock;
import cn.toesbieya.jxc.stock.model.vo.StockExport;
import cn.toesbieya.jxc.common.model.vo.StockSearchResult;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface StockMapper extends BaseMapper<BizStock> {
    List<StockSearchResult> search(@Param(Constants.WRAPPER) Wrapper<BizStock> wrapper);

    List<StockExport> export(@Param(Constants.WRAPPER) Wrapper<BizStock> wrapper);

    int insertBatch(@Param("list") List<BizStock> list);

    int outbound(@Param("id") Integer id, @Param("num") BigDecimal num);
}

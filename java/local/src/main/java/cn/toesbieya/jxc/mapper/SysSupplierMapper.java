package cn.toesbieya.jxc.mapper;

import cn.toesbieya.jxc.model.entity.SysSupplier;
import cn.toesbieya.jxc.model.vo.result.RegionValueResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface SysSupplierMapper extends BaseMapper<SysSupplier> {
    List<RegionValueResult> getLimitRegion();
}

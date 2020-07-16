package cn.toesbieya.jxc.system.mapper;

import cn.toesbieya.jxc.common.model.entity.SysSupplier;
import cn.toesbieya.jxc.system.model.vo.RegionValueResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface SysSupplierMapper extends BaseMapper<SysSupplier> {
    List<RegionValueResult> getLimitRegion();
}

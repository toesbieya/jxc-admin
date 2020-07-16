package cn.toesbieya.jxc.system.mapper;

import cn.toesbieya.jxc.common.model.entity.SysCustomer;
import cn.toesbieya.jxc.system.model.vo.RegionValueResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface SysCustomerMapper extends BaseMapper<SysCustomer> {
    List<RegionValueResult> getLimitRegion();
}

package cn.toesbieya.jxc.mapper;

import cn.toesbieya.jxc.model.entity.SysCustomer;
import cn.toesbieya.jxc.model.vo.result.RegionValueResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface SysCustomerMapper extends BaseMapper<SysCustomer> {
    List<RegionValueResult> getLimitRegion();
}
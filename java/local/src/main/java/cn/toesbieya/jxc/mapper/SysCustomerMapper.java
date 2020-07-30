package cn.toesbieya.jxc.mapper;

import cn.toesbieya.jxc.model.entity.SysCustomer;
import cn.toesbieya.jxc.model.vo.result.RegionValueResult;
import cn.toesbieya.jxc.model.vo.search.CustomerSearch;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysCustomerMapper {
    List<SysCustomer> get();

    SysCustomer getById(int id);

    List<SysCustomer> search(CustomerSearch vo);

    int add(SysCustomer category);

    int update(SysCustomer category);

    int del(int id);

    List<RegionValueResult> getLimitRegion();

    boolean isNameExist(@Param("name") String name, @Param("id") Integer id);
}
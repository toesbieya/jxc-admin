package com.toesbieya.my.mapper;

import com.toesbieya.my.model.entity.SysCustomer;
import com.toesbieya.my.model.vo.result.RegionValueResult;
import com.toesbieya.my.model.vo.search.CustomerSearch;
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
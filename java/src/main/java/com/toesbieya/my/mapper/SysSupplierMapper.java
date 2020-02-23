package com.toesbieya.my.mapper;

import com.toesbieya.my.model.entity.SysSupplier;
import com.toesbieya.my.model.vo.result.RegionValueResult;
import com.toesbieya.my.model.vo.search.SupplierSearch;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysSupplierMapper {
    List<SysSupplier> get();

    SysSupplier getById(int id);

    List<SysSupplier> search(SupplierSearch vo);

    int add(SysSupplier category);

    int update(SysSupplier category);

    int del(int id);

    List<RegionValueResult> getLimitRegion();

    boolean isNameExist(@Param("name") String name, @Param("id") Integer id);
}

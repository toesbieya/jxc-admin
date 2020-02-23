package com.toesbieya.my.mapper;

import com.toesbieya.my.model.entity.SysCategory;
import com.toesbieya.my.model.vo.search.CategorySearch;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysCategoryMapper {
    List<SysCategory> getAll();

    List<SysCategory> search(CategorySearch vo);

    SysCategory getById(@Param("id") int id);

    int add(SysCategory sysCategory);

    int update(SysCategory sysCategory);

    int del(@Param("id") int id);

    boolean hasChildren(@Param("id") int id);

    boolean isNameExist(@Param("name") String name, @Param("id") Integer id);

    boolean checkIsUse(@Param("cid") Integer cid);
}

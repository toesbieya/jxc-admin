package com.toesbieya.my.mapper;

import com.toesbieya.my.model.entity.SysRole;
import com.toesbieya.my.model.vo.search.RoleSearch;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleMapper {
    List<SysRole> get();

    List<SysRole> getAll();

    List<SysRole> search(RoleSearch vo);

    int add(SysRole role);

    int update(SysRole role);

    int del(int id);

    boolean isNameExist(@Param("name") String name, @Param("id") Integer id);
}

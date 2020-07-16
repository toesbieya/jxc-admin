package com.toesbieya.my.mapper;

import com.toesbieya.my.model.entity.SysDepartment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysDepartmentMapper {
    List<SysDepartment> get();

    List<SysDepartment> getAll();

    int add(SysDepartment department);

    int update(SysDepartment department);

    int del(@Param("id") int id);

    boolean nameExist(@Param("id") Integer id, @Param("pid") int pid, @Param("name") String name);
}

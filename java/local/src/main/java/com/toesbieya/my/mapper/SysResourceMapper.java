package com.toesbieya.my.mapper;

import com.toesbieya.my.model.entity.SysResource;

import java.util.List;

public interface SysResourceMapper {
    List<SysResource> get();

    List<SysResource> getAll();

    List<SysResource> getByRole(int role);

    int update(SysResource resource);
}

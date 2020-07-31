package cn.toesbieya.jxc.mapper;

import cn.toesbieya.jxc.model.entity.SysRole;
import cn.toesbieya.jxc.model.vo.search.RoleSearch;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleMapper {
    SysRole selectById(@Param("id") int id);

    List<SysRole> get();

    List<SysRole> getAll();

    List<SysRole> search(RoleSearch vo);

    int insert(SysRole role);

    int update(SysRole role);

    int del(int id);

    boolean isNameExist(@Param("name") String name, @Param("id") Integer id);
}

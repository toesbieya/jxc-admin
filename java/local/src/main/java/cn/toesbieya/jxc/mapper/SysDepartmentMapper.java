package cn.toesbieya.jxc.mapper;

import cn.toesbieya.jxc.model.entity.SysDepartment;
import cn.toesbieya.jxc.model.vo.DepartmentVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysDepartmentMapper {
    List<DepartmentVo> selectChildrenById(@Param("id") int id);

    List<DepartmentVo> selectParentsById(@Param("id") int id);

    int insert(SysDepartment department);

    int update(SysDepartment department);

    int del(@Param("id") int id);

    boolean nameExist(@Param("id") Integer id, @Param("pid") int pid, @Param("name") String name);
}

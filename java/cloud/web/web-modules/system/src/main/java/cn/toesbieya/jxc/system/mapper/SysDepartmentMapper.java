package cn.toesbieya.jxc.system.mapper;

import cn.toesbieya.jxc.common.model.entity.SysDepartment;
import cn.toesbieya.jxc.common.model.vo.DepartmentVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysDepartmentMapper extends BaseMapper<SysDepartment> {
    List<DepartmentVo> selectChildrenById(@Param("id") int id);

    List<DepartmentVo> selectParentsById(@Param("id") int id);
}

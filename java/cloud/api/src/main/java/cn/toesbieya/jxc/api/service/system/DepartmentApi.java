package cn.toesbieya.jxc.api.service.system;

import cn.toesbieya.jxc.common.model.entity.SysUser;
import cn.toesbieya.jxc.common.model.vo.DepartmentVo;

import java.util.Set;

public interface DepartmentApi {
    //获取用户的数据范围（部门ID集合），返回null说明无需限制
    Set<Integer> getUserDataScope(SysUser user);

    //获取带有全名的部门
    DepartmentVo getById(int id);
}

package cn.toesbieya.jxc.service;

import cn.toesbieya.jxc.annoation.UserAction;
import cn.toesbieya.jxc.enumeration.DataScopeEnum;
import cn.toesbieya.jxc.mapper.SysDepartmentMapper;
import cn.toesbieya.jxc.mapper.SysRoleMapper;
import cn.toesbieya.jxc.model.entity.SysDepartment;
import cn.toesbieya.jxc.model.entity.SysRole;
import cn.toesbieya.jxc.model.entity.SysUser;
import cn.toesbieya.jxc.model.vo.DepartmentVo;
import cn.toesbieya.jxc.model.vo.Result;
import cn.toesbieya.jxc.utils.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SysDepartmentService {
    @Resource
    private SysDepartmentMapper departmentMapper;
    @Resource
    private SysRoleMapper roleMapper;

    public List<DepartmentVo> getAll() {
        return departmentMapper.selectChildrenById(1);
    }

    public DepartmentVo getById(int id) {
        List<DepartmentVo> list = departmentMapper.selectParentsById(id);
        DepartmentVo parent = Util.find(list, i -> i.getPid().equals(0));
        DepartmentVo child = Util.find(list, i -> i.getId().equals(id));
        if (parent == null || child == null) {
            return null;
        }
        child.setFullname(parent.getFullname());
        return child;
    }

    //获取用户的数据范围（部门ID集合），返回null说明无需限制
    public Set<Integer> getUserDataScope(SysUser user) {
        Integer roleId = user.getRole();

        if (user.isAdmin()) return null;
        if (roleId == null) return Collections.emptySet();

        SysRole role = roleMapper.selectById(roleId);

        if (role == null) return Collections.emptySet();

        int scope = role.getScope();

        //全部
        if (scope == DataScopeEnum.ALL.getCode()) {
            return null;
        }

        //本部门
        if (scope == DataScopeEnum.SELF.getCode()) {
            Integer dept = user.getDept();
            if (dept == null) return Collections.emptySet();
            return departmentMapper
                    .selectChildrenById(dept)
                    .stream()
                    .map(DepartmentVo::getId)
                    .collect(Collectors.toSet());
        }

        //指定部门
        if (scope == DataScopeEnum.SPECIFIC.getCode()) {
            String deptIds = role.getDepartmentId();
            if (StringUtils.isEmpty(deptIds)) {
                return Collections.emptySet();
            }
            return Arrays
                    .stream(deptIds.split(","))
                    .map(Integer::valueOf)
                    .collect(Collectors.toSet());
        }

        return Collections.emptySet();
    }

    @UserAction("'添加部门：'+#department.name")
    public Result add(SysDepartment department) {
        if (departmentMapper.nameExist(null, department.getPid(), department.getName())) {
            return Result.fail("添加失败，部门名称重复");
        }
        int rows = departmentMapper.insert(department);
        return rows > 0 ? Result.success("添加成功") : Result.fail("添加失败");
    }

    @UserAction("'修改部门：'+#department.name")
    public Result update(SysDepartment department) {
        if (departmentMapper.nameExist(department.getId(), department.getPid(), department.getName())) {
            return Result.fail("修改失败，部门名称重复");
        }
        departmentMapper.update(department);
        return Result.success("修改成功");
    }

    @UserAction("'删除部门：'+#department.name")
    public Result del(SysDepartment department) {
        int rows = departmentMapper.del(department.getId());
        return rows > 0 ? Result.success("删除成功") : Result.fail("删除失败，请刷新重试");
    }
}

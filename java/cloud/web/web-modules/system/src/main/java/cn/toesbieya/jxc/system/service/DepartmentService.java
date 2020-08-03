package cn.toesbieya.jxc.system.service;

import cn.toesbieya.jxc.api.system.DepartmentApi;
import cn.toesbieya.jxc.common.enumeration.DataScopeEnum;
import cn.toesbieya.jxc.common.model.entity.SysDepartment;
import cn.toesbieya.jxc.common.model.entity.SysRole;
import cn.toesbieya.jxc.common.model.entity.SysUser;
import cn.toesbieya.jxc.common.model.vo.DepartmentVo;
import cn.toesbieya.jxc.common.model.vo.Result;
import cn.toesbieya.jxc.common.util.Util;
import cn.toesbieya.jxc.system.mapper.SysDepartmentMapper;
import cn.toesbieya.jxc.web.common.annoation.UserAction;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@org.apache.dubbo.config.annotation.Service
public class DepartmentService implements DepartmentApi {
    @Resource
    private SysDepartmentMapper departmentMapper;
    @Resource
    private RoleService roleService;

    public List<DepartmentVo> getAll() {
        return departmentMapper.selectChildrenById(1);
    }

    @Override
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

    @Override
    public Set<Integer> getUserDataScope(SysUser user) {
        if (user.isAdmin()) return null;

        SysRole role = roleService.getRoleById(user.getRole());

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
        if (isNameExist(null, department.getPid(), department.getName())) {
            return Result.fail("添加失败，存在名称相同的同级部门");
        }
        int rows = departmentMapper.insert(department);
        return rows > 0 ? Result.success("添加成功") : Result.fail("添加失败");
    }

    @UserAction("'修改部门：'+#department.name")
    public Result update(SysDepartment department) {
        if (isNameExist(department.getId(), department.getPid(), department.getName())) {
            return Result.fail("修改失败，存在名称相同的同级部门");
        }
        departmentMapper.updateById(department);
        return Result.success("修改成功");
    }

    @UserAction("'删除部门：'+#department.name")
    public Result del(SysDepartment department) {
        int rows = departmentMapper.deleteById(department.getId());
        return rows > 0 ? Result.success("删除成功") : Result.fail("删除失败，请刷新重试");
    }

    //判断同级节点中是否有相同的部门名称
    private boolean isNameExist(Integer id, Integer pid, String name) {
        Integer num = departmentMapper.selectCount(
                Wrappers.lambdaQuery(SysDepartment.class)
                        .eq(SysDepartment::getName, name)
                        .ne(id != null, SysDepartment::getId, id)
                        .eq(SysDepartment::getPid, pid)
        );
        return num != null && num > 0;
    }
}

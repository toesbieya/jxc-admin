package cn.toesbieya.jxc.service;

import cn.toesbieya.jxc.annoation.UserAction;
import cn.toesbieya.jxc.model.entity.SysDepartment;
import cn.toesbieya.jxc.mapper.SysDepartmentMapper;
import cn.toesbieya.jxc.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class SysDepartmentService {
    @Resource
    private SysDepartmentMapper departmentMapper;

    public List<SysDepartment> get() {
        return departmentMapper.get();
    }

    public List<SysDepartment> getAll() {
        return departmentMapper.getAll();
    }

    @UserAction("'添加部门：'+#department.name")
    public Result add(SysDepartment department) {
        if (departmentMapper.nameExist(null, department.getPid(), department.getName())) {
            return Result.fail("添加失败，部门名称重复");
        }
        int rows = departmentMapper.add(department);
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

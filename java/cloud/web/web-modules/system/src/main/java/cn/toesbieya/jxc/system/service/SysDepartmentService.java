package cn.toesbieya.jxc.system.service;

import cn.toesbieya.jxc.common.model.entity.SysDepartment;
import cn.toesbieya.jxc.common.model.vo.Result;
import cn.toesbieya.jxc.system.mapper.SysDepartmentMapper;
import cn.toesbieya.jxc.web.common.annoation.UserAction;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SysDepartmentService {
    @Resource
    private SysDepartmentMapper departmentMapper;

    public List<SysDepartment> get() {
        return departmentMapper.selectList(
                Wrappers.lambdaQuery(SysDepartment.class)
                        .eq(SysDepartment::getStatus, 1)
        );
    }

    public List<SysDepartment> getAll() {
        return departmentMapper.selectList(null);
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

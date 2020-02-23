package com.toesbieya.my.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageSerializable;
import com.toesbieya.my.annoation.UserAction;
import com.toesbieya.my.mapper.SysRoleMapper;
import com.toesbieya.my.model.entity.SysRole;
import com.toesbieya.my.model.vo.search.RoleSearch;
import com.toesbieya.my.utils.Result;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SysRoleService {
    @Resource
    private SysRoleMapper roleMapper;

    public List<SysRole> get() {
        return roleMapper.get();
    }

    public List<SysRole> getAll() {
        return roleMapper.getAll();
    }

    public PageSerializable<SysRole> search(RoleSearch vo) {
        PageHelper.startPage(vo.getPage(), vo.getPageSize());
        return new PageSerializable<>(roleMapper.search(vo));
    }

    @UserAction("'添加角色：'+#role.name")
    public Result add(SysRole role) {
        if (roleMapper.isNameExist(role.getName(), null)) {
            return Result.fail("添加失败，角色名称重复");
        }
        int rows = roleMapper.add(role);
        return rows > 0 ? Result.success("添加成功") : Result.fail("添加失败");
    }

    @UserAction("'修改角色：'+#role.name")
    public Result update(SysRole role) {
        if (roleMapper.isNameExist(role.getName(), role.getId())) {
            return Result.fail("修改失败，角色名称重复");
        }
        roleMapper.update(role);
        return Result.success("修改成功");
    }

    @UserAction("'删除角色：'+#role.name")
    public Result del(SysRole role) {
        int rows = roleMapper.del(role.getId());
        return rows > 0 ? Result.success("删除成功") : Result.fail("删除失败，请刷新重试");
    }
}

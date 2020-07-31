package cn.toesbieya.jxc.service;

import cn.toesbieya.jxc.annoation.Lock;
import cn.toesbieya.jxc.annoation.UserAction;
import cn.toesbieya.jxc.model.entity.SysCategory;
import cn.toesbieya.jxc.model.vo.search.CategorySearch;
import cn.toesbieya.jxc.mapper.SysCategoryMapper;
import cn.toesbieya.jxc.model.vo.Result;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SysCategoryService {
    @Resource
    private SysCategoryMapper categoryMapper;

    public List<SysCategory> getAll() {
        return categoryMapper.getAll();
    }

    public List<SysCategory> search(CategorySearch vo) {
        return categoryMapper.search(vo);
    }

    @UserAction("'添加分类：'+#category.name")
    @Lock("'category'+#category.pid")
    public Result add(SysCategory category) {
        String err = check(category);
        if (err != null) return Result.fail("添加失败，" + err);

        int rows = categoryMapper.add(category);
        return rows > 0 ? Result.success("添加成功") : Result.fail("添加失败");
    }

    @UserAction("'修改分类：'+#category.name")
    @Lock("'category'+#category.id")
    public Result update(SysCategory category) {
        String err = check(category);
        if (err != null) return Result.fail("修改失败，" + err);

        int rows = categoryMapper.update(category);
        return rows > 0 ? Result.success("修改成功") : Result.fail("修改失败，请刷新后重试");
    }

    @UserAction("'删除分类：'+#category.name")
    @Lock("'category'+#category.id")
    public Result del(SysCategory category) {
        Integer cid = category.getId();
        if (categoryMapper.hasChildren(cid)) {
            return Result.fail("请先删除该分类下的子节点");
        }
        if (categoryMapper.checkIsUse(cid)) {
            return Result.fail("该分类已在业务中使用，不可删除");
        }
        int rows = categoryMapper.del(cid);
        return rows > 0 ? Result.success("删除成功") : Result.fail("删除失败，请刷新后重试");
    }

    private String check(SysCategory category) {
        if (category.getPid() != 0) {
            SysCategory parent = categoryMapper.getById(category.getPid());
            if (parent == null || parent.getType() == 1) return "父节点状态变更，请刷新后重试";
        }
        if (categoryMapper.isNameExist(category.getName(), category.getId())) {
            return "分类【" + category.getName() + "】已存在";
        }

        if (category.getId() != null && category.getType() == 1) {
            if (categoryMapper.hasChildren(category.getId())) {
                return "该分类下存在子节点，不能改为实体类型";
            }
        }
        return null;
    }
}

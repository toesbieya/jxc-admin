package cn.toesbieya.jxc.system.service;

import cn.toesbieya.jxc.common.model.entity.SysCategory;
import cn.toesbieya.jxc.common.model.vo.Result;
import cn.toesbieya.jxc.system.mapper.SysCategoryMapper;
import cn.toesbieya.jxc.system.model.vo.CategorySearch;
import cn.toesbieya.jxc.web.common.annoation.Lock;
import cn.toesbieya.jxc.web.common.annoation.UserAction;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class SysCategoryService {
    @Resource
    private SysCategoryMapper categoryMapper;

    public List<SysCategory> getAll() {
        return categoryMapper.selectList(null);
    }

    public List<SysCategory> search(CategorySearch vo) {
        Integer id = vo.getId();
        String ids = vo.getIds();
        Integer pid = vo.getPid();
        String pids = vo.getPids();
        String name = vo.getName();
        Integer type = vo.getType();
        Long startTime = vo.getStartTime();
        Long endTime = vo.getEndTime();

        return categoryMapper.selectList(
                Wrappers.lambdaQuery(SysCategory.class)
                        .eq(id != null, SysCategory::getId, id)
                        .inSql(!StringUtils.isEmpty(ids), SysCategory::getId, ids)
                        .eq(pid != null, SysCategory::getPid, pid)
                        .inSql(!StringUtils.isEmpty(pids), SysCategory::getPid, pids)
                        .like(!StringUtils.isEmpty(name), SysCategory::getName, name)
                        .eq(type != null, SysCategory::getType, type)
                        .ge(startTime != null, SysCategory::getCtime, startTime)
                        .le(endTime != null, SysCategory::getCtime, endTime)
                        .orderByDesc(SysCategory::getCtime)
        );
    }

    @UserAction("'添加分类：'+#category.name")
    @Lock("'category'+#category.pid")
    public Result add(SysCategory category) {
        String err = check(category);
        if (err != null) return Result.fail("添加失败，" + err);

        int rows = categoryMapper.insert(category);
        return rows > 0 ? Result.success("添加成功") : Result.fail("添加失败");
    }

    @UserAction("'修改分类：'+#category.name")
    @Lock("'category'+#category.id")
    public Result update(SysCategory category) {
        String err = check(category);
        if (err != null) return Result.fail("修改失败，" + err);

        int rows = categoryMapper.update(
                null,
                Wrappers.lambdaUpdate(SysCategory.class)
                        .set(SysCategory::getPid, category.getPid())
                        .set(SysCategory::getName, category.getName())
                        .set(SysCategory::getType, category.getType())
                        .eq(SysCategory::getId, category.getId())
        );
        return rows > 0 ? Result.success("修改成功") : Result.fail("修改失败，请刷新后重试");
    }

    @UserAction("'删除分类：'+#category.name")
    @Lock("'category'+#category.id")
    public Result del(SysCategory category) {
        Integer cid = category.getId();

        if (hasChildren(cid)) {
            return Result.fail("请先删除该分类下的子节点");
        }

        if (categoryMapper.checkIsUse(cid)) {
            return Result.fail("该分类已在业务中使用，不可删除");
        }

        int rows = categoryMapper.deleteById(cid);
        return rows > 0 ? Result.success("删除成功") : Result.fail("删除失败，请刷新后重试");
    }

    private String check(SysCategory category) {
        //当该分类不是顶级节点时，判断其父节点是否允许有下级
        if (category.getPid() != 0) {
            SysCategory parent = categoryMapper.selectById(category.getPid());
            if (parent == null || parent.getType() == 1) {
                return "父节点状态变更，请刷新后重试";
            }
        }

        Integer id = category.getId();

        Integer exist = categoryMapper.selectCount(
                Wrappers.lambdaQuery(SysCategory.class)
                        .eq(SysCategory::getName, category.getName())
                        .ne(id != null, SysCategory::getId, id)
        );
        if (exist != null && exist > 0) {
            return String.format("分类【%s】已存在", category.getName());
        }

        Integer cid = category.getId();
        if (cid != null && category.getType() == 1 && hasChildren(cid)) {
            return "该分类下存在子节点，不能改为实体类型";
        }

        return null;
    }

    private boolean hasChildren(Integer id) {
        Integer checkNum = categoryMapper.selectCount(
                Wrappers.lambdaQuery(SysCategory.class)
                        .eq(SysCategory::getPid, id)
        );

        return checkNum != null && checkNum > 0;
    }
}

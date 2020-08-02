package cn.toesbieya.jxc.service.sys;

import cn.toesbieya.jxc.annoation.Lock;
import cn.toesbieya.jxc.annoation.UserAction;
import cn.toesbieya.jxc.mapper.SysCategoryMapper;
import cn.toesbieya.jxc.model.entity.SysCategory;
import cn.toesbieya.jxc.model.vo.Result;
import cn.toesbieya.jxc.model.vo.search.CategorySearch;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SysCategoryService {
    @Resource
    private SysCategoryMapper mapper;

    public List<SysCategory> getAll() {
        return mapper.selectList(null);
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

        return mapper.selectList(
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

        int rows = mapper.insert(category);
        return rows > 0 ? Result.success("添加成功") : Result.fail("添加失败");
    }

    @UserAction("'修改分类：'+#category.name")
    @Lock("'category'+#category.id")
    public Result update(SysCategory category) {
        String err = check(category);
        if (err != null) return Result.fail("修改失败，" + err);

        int rows = mapper.update(
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
        if (mapper.checkIsUse(cid)) {
            return Result.fail("该分类已在业务中使用，不可删除");
        }
        int rows = mapper.deleteById(cid);
        return rows > 0 ? Result.success("删除成功") : Result.fail("删除失败，请刷新后重试");
    }

    private String check(SysCategory category) {
        //当该分类不是顶级节点时，判断其父节点是否允许有下级
        if (!category.getPid().equals(0)) {
            SysCategory parent = mapper.selectById(category.getPid());
            if (parent == null || parent.getType().equals(1)) {
                return "父节点状态变更，请刷新后重试";
            }
        }
        Integer id = category.getId();
        Integer exist = mapper.selectCount(
                Wrappers.lambdaQuery(SysCategory.class)
                        .eq(SysCategory::getName, category.getName())
                        .ne(id != null, SysCategory::getId, id)
        );
        if (exist != null && exist > 0) {
            return String.format("分类【%s】已存在", category.getName());
        }
        if (id != null && category.getType().equals(1) && hasChildren(id)) {
            return "该分类下存在子节点，不能改为实体类型";
        }
        return null;
    }

    private boolean hasChildren(Integer id) {
        Integer checkNum = mapper.selectCount(
                Wrappers.lambdaQuery(SysCategory.class)
                        .eq(SysCategory::getPid, id)
        );

        return checkNum != null && checkNum > 0;
    }
}

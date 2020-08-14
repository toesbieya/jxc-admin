package cn.toesbieya.jxc.service.sys;

import cn.toesbieya.jxc.annoation.Lock;
import cn.toesbieya.jxc.annoation.UserAction;
import cn.toesbieya.jxc.mapper.SysCategoryMapper;
import cn.toesbieya.jxc.model.entity.SysCategory;
import cn.toesbieya.jxc.model.vo.R;
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
        Boolean leaf = vo.getLeaf();
        Long startTime = vo.getStartTime();
        Long endTime = vo.getEndTime();

        return mapper.selectList(
                Wrappers.lambdaQuery(SysCategory.class)
                        .eq(id != null, SysCategory::getId, id)
                        .inSql(!StringUtils.isEmpty(ids), SysCategory::getId, ids)
                        .eq(pid != null, SysCategory::getPid, pid)
                        .inSql(!StringUtils.isEmpty(pids), SysCategory::getPid, pids)
                        .like(!StringUtils.isEmpty(name), SysCategory::getName, name)
                        .eq(leaf != null, SysCategory::isLeaf, leaf)
                        .ge(startTime != null, SysCategory::getCtime, startTime)
                        .le(endTime != null, SysCategory::getCtime, endTime)
                        .orderByDesc(SysCategory::getCtime)
        );
    }

    @UserAction("'添加分类：'+#category.name")
    @Lock("'category'+#category.pid")
    public R add(SysCategory category) {
        String err = check(category);
        if (err != null) return R.fail("添加失败，" + err);

        int rows = mapper.insert(category);
        return rows > 0 ? R.success("添加成功") : R.fail("添加失败");
    }

    @UserAction("'修改分类：'+#category.name")
    @Lock("'category'+#category.id")
    public R update(SysCategory category) {
        String err = check(category);
        if (err != null) return R.fail("修改失败，" + err);

        int rows = mapper.update(
                null,
                Wrappers.lambdaUpdate(SysCategory.class)
                        .set(SysCategory::getPid, category.getPid())
                        .set(SysCategory::getName, category.getName())
                        .set(SysCategory::isLeaf, category.isLeaf())
                        .eq(SysCategory::getId, category.getId())
        );
        return rows > 0 ? R.success("修改成功") : R.fail("修改失败，请刷新后重试");
    }

    @UserAction("'删除分类：'+#category.name")
    @Lock("'category'+#category.id")
    public R del(SysCategory category) {
        Integer cid = category.getId();
        if (hasChildren(cid)) {
            return R.fail("请先删除该分类下的子节点");
        }
        if (mapper.checkIsUse(cid)) {
            return R.fail("该分类已在业务中使用，不可删除");
        }
        int rows = mapper.deleteById(cid);
        return rows > 0 ? R.success("删除成功") : R.fail("删除失败，请刷新后重试");
    }

    private String check(SysCategory category) {
        //当该分类不是顶级节点时，判断其父节点是否允许有下级
        if (!category.getPid().equals(0)) {
            SysCategory parent = mapper.selectById(category.getPid());
            if (parent == null || parent.isLeaf()) {
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
        if (id != null && category.isLeaf() && hasChildren(id)) {
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

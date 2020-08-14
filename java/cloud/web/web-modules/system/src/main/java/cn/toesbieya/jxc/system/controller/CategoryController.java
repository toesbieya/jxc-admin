package cn.toesbieya.jxc.system.controller;

import cn.toesbieya.jxc.common.model.entity.SysCategory;
import cn.toesbieya.jxc.common.model.vo.R;
import cn.toesbieya.jxc.system.model.vo.CategorySearch;
import cn.toesbieya.jxc.system.service.CategoryService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("category")
public class CategoryController {
    @Resource
    private CategoryService service;

    @GetMapping("getAll")
    public R getAll() {
        return R.success(service.getAll());
    }

    @PostMapping("search")
    public R search(@RequestBody CategorySearch vo) {
        return R.success(service.search(vo));
    }

    @PostMapping("add")
    public R add(@RequestBody SysCategory category) {
        String errMsg = validateCategoryCreateParam(category);
        if (errMsg != null) return R.fail(errMsg);

        category.setId(null);
        category.setCtime(System.currentTimeMillis());

        return service.add(category);
    }

    @PostMapping("update")
    public R update(@RequestBody SysCategory category) {
        String errMsg = validateCategoryUpdateParam(category);
        if (errMsg != null) return R.fail(errMsg);

        return service.update(category);
    }

    @PostMapping("del")
    public R del(@RequestBody SysCategory category) {
        if (category.getId() == null) {
            return R.fail("删除失败，参数错误");
        }
        return service.del(category);
    }

    private String validateCategoryCreateParam(SysCategory category) {
        if (category.getPid() == null) {
            return "创建失败，参数错误";
        }
        if (StringUtils.isEmpty(category.getName())) {
            return "创建失败，分类名称不能为空";
        }
        return null;
    }

    private String validateCategoryUpdateParam(SysCategory category) {
        if (category.getId() == null || category.getPid() == null) {
            return "修改失败，参数错误";
        }
        if (StringUtils.isEmpty(category.getName())) {
            return "创建失败，分类名称不能为空";
        }
        return null;
    }
}

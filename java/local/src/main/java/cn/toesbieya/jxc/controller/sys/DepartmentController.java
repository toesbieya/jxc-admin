package cn.toesbieya.jxc.controller.sys;

import cn.toesbieya.jxc.model.entity.SysDepartment;
import cn.toesbieya.jxc.model.vo.DepartmentVo;
import cn.toesbieya.jxc.model.vo.R;
import cn.toesbieya.jxc.service.sys.SysDepartmentService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("system/department")
public class DepartmentController {
    @Resource
    private SysDepartmentService service;

    @GetMapping("get")
    public R get(boolean all) {
        List<DepartmentVo> list = service.getAll();
        if (!all) {
            list = list
                    .stream()
                    .filter(SysDepartment::isEnable)
                    .collect(Collectors.toList());
        }
        return R.success(list);
    }

    @PostMapping("add")
    public R add(@RequestBody SysDepartment department) {
        if (null == department.getPid() || StringUtils.isEmpty(department.getName())) {
            return R.fail("添加失败，参数错误");
        }
        return service.add(department);
    }

    @PostMapping("update")
    public R update(@RequestBody SysDepartment department) {
        if (null == department.getId()
                || null == department.getPid()
                || StringUtils.isEmpty(department.getName())) {
            return R.fail("修改失败，参数错误");
        }
        return service.update(department);
    }

    @PostMapping("del")
    public R del(@RequestBody SysDepartment department) {
        if (null == department.getId() || StringUtils.isEmpty(department.getName())) {
            return R.fail("删除失败，参数错误");
        }
        return service.del(department);
    }
}

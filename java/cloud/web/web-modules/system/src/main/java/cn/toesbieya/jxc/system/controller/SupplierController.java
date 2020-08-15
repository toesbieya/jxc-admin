package cn.toesbieya.jxc.system.controller;

import cn.toesbieya.jxc.common.model.entity.SysSupplier;
import cn.toesbieya.jxc.common.model.vo.R;
import cn.toesbieya.jxc.system.model.vo.SupplierSearch;
import cn.toesbieya.jxc.system.service.SupplierService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("supplier")
public class SupplierController {
    @Resource
    private SupplierService service;

    @GetMapping("getLimitRegion")
    public R getLimitRegion() {
        return R.success(service.getLimitRegion());
    }

    @PostMapping("search")
    public R search(@RequestBody SupplierSearch vo) {
        return R.success(service.search(vo));
    }

    @PostMapping("add")
    public R add(@RequestBody SysSupplier supplier) {
        String errMsg = validateCreateParam(supplier);
        if (errMsg != null) return R.fail("创建失败，" + errMsg);

        supplier.setId(null);
        supplier.setCtime(System.currentTimeMillis());

        return service.add(supplier);
    }

    @PostMapping("update")
    public R update(@RequestBody SysSupplier supplier) {
        String errMsg = validateUpdateParam(supplier);
        return errMsg == null ? service.update(supplier) : R.fail("修改失败，" + errMsg);
    }

    @PostMapping("del")
    public R del(@RequestBody SysSupplier supplier) {
        if (supplier.getId() == null) return R.fail("删除失败，参数错误");
        return service.del(supplier);
    }

    private String validateCreateParam(SysSupplier supplier) {
        if (StringUtils.isEmpty(supplier.getName())) return "供应商名称不能为空";
        if (StringUtils.isEmpty(supplier.getRegion())) return "供应商行政区域不能为空";
        if (StringUtils.isEmpty(supplier.getAddress())) return "供应商地址不能为空";
        if (StringUtils.isEmpty(supplier.getLinkman())) return "供应商联系人不能为空";
        if (StringUtils.isEmpty(supplier.getLinkphone())) return "供应商联系电话不能为空";
        return null;
    }

    private String validateUpdateParam(SysSupplier supplier) {
        if (supplier.getId() == null) return "参数错误";
        return validateCreateParam(supplier);
    }
}

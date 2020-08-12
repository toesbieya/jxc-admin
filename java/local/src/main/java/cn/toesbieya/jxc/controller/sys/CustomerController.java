package cn.toesbieya.jxc.controller.sys;

import cn.toesbieya.jxc.model.entity.SysCustomer;
import cn.toesbieya.jxc.model.vo.search.CustomerSearch;
import cn.toesbieya.jxc.service.sys.SysCustomerService;
import cn.toesbieya.jxc.model.vo.R;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("system/customer")
public class CustomerController {
    @Resource
    private SysCustomerService service;

    @GetMapping("getLimitRegion")
    public R getLimitRegion() {
        return R.success(service.getLimitRegion());
    }

    @PostMapping("search")
    public R search(@RequestBody CustomerSearch vo) {
        return R.success(service.search(vo));
    }

    @PostMapping("add")
    public R add(@RequestBody SysCustomer customer) {
        String errMsg = validateCreateParam(customer);
        if (errMsg != null) return R.fail("创建失败，" + errMsg);

        customer.setId(null);
        customer.setCtime(System.currentTimeMillis());
        return service.add(customer);
    }

    @PostMapping("update")
    public R update(@RequestBody SysCustomer customer) {
        String errMsg = validateUpdateParam(customer);
        if (errMsg != null) return R.fail("修改失败，" + errMsg);

        return service.update(customer);
    }

    @PostMapping("del")
    public R del(@RequestBody SysCustomer customer) {
        if (customer.getId() == null) return R.fail("删除失败，参数错误");
        return service.del(customer);
    }

    private String validateCreateParam(SysCustomer customer) {
        if (StringUtils.isEmpty(customer.getName())) return "客户名称不能为空";
        if (StringUtils.isEmpty(customer.getRegion())) return "客户行政区域不能为空";
        if (StringUtils.isEmpty(customer.getAddress())) return "客户地址不能为空";
        if (StringUtils.isEmpty(customer.getLinkman())) return "客户联系人不能为空";
        if (StringUtils.isEmpty(customer.getLinkphone())) return "客户联系电话不能为空";
        if (customer.getStatus() == null) return "客户状态不能为空";
        return null;
    }

    private String validateUpdateParam(SysCustomer customer) {
        if (customer.getId() == null) return "参数错误";
        return validateCreateParam(customer);
    }
}

package cn.toesbieya.jxc.service;

import cn.toesbieya.jxc.annoation.UserAction;
import cn.toesbieya.jxc.model.entity.SysCustomer;
import cn.toesbieya.jxc.model.vo.result.PageResult;
import cn.toesbieya.jxc.model.vo.result.RegionValueResult;
import cn.toesbieya.jxc.model.vo.search.CustomerSearch;
import com.github.pagehelper.PageHelper;
import cn.toesbieya.jxc.mapper.SysCustomerMapper;
import cn.toesbieya.jxc.utils.Result;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SysCustomerService {
    @Resource
    private SysCustomerMapper customerMapper;

    public List<RegionValueResult> getLimitRegion() {
        return customerMapper.getLimitRegion();
    }

    public List<SysCustomer> get() {
        return customerMapper.get();
    }

    public PageResult<SysCustomer> search(CustomerSearch vo) {
        PageHelper.startPage(vo.getPage(), vo.getPageSize());
        List<SysCustomer> SysCustomers = customerMapper.search(vo);
        return new PageResult<>(SysCustomers);
    }

    @UserAction("'添加客户：'+ #customer.name")
    public Result add(SysCustomer customer) {
        if (customerMapper.isNameExist(customer.getName(), null)) {
            return Result.fail("添加失败，客户【" + customer.getName() + "】已存在");
        }
        int rows = customerMapper.add(customer);
        return rows > 0 ? Result.success("添加成功") : Result.fail("添加失败");
    }

    @UserAction("'修改客户：'+ #customer.name")
    public Result update(SysCustomer customer) {
        if (customerMapper.isNameExist(customer.getName(), customer.getId())) {
            return Result.fail("修改失败，客户【" + customer.getName() + "】已存在");
        }
        int rows = customerMapper.update(customer);
        return rows > 0 ? Result.success("修改成功") : Result.fail("修改失败，请刷新重试");
    }

    @UserAction("删除客户：'+ #customer.name")
    public Result del(SysCustomer customer) {
        int rows = customerMapper.del(customer.getId());
        return rows > 0 ? Result.success("删除成功") : Result.fail("删除失败，请刷新重试");
    }
}

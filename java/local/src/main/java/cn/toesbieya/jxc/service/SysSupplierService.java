package cn.toesbieya.jxc.service;

import cn.toesbieya.jxc.annoation.UserAction;
import cn.toesbieya.jxc.model.entity.SysSupplier;
import cn.toesbieya.jxc.model.vo.result.PageResult;
import cn.toesbieya.jxc.model.vo.result.RegionValueResult;
import cn.toesbieya.jxc.model.vo.search.SupplierSearch;
import com.github.pagehelper.PageHelper;
import cn.toesbieya.jxc.mapper.SysSupplierMapper;
import cn.toesbieya.jxc.utils.Result;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SysSupplierService {
    @Resource
    private SysSupplierMapper supplierMapper;

    public List<RegionValueResult> getLimitRegion() {
        return supplierMapper.getLimitRegion();
    }

    public List<SysSupplier> get() {
        return supplierMapper.get();
    }

    public PageResult<SysSupplier> search(SupplierSearch vo) {
        PageHelper.startPage(vo.getPage(), vo.getPageSize());
        List<SysSupplier> sysSuppliers = supplierMapper.search(vo);
        return new PageResult<>(sysSuppliers);
    }

    @UserAction("'添加供应商：'+ #supplier.name")
    public Result add(SysSupplier supplier) {
        if (supplierMapper.isNameExist(supplier.getName(), null)) {
            return Result.fail("添加失败，供应商【" + supplier.getName() + "】已存在");
        }
        int rows = supplierMapper.add(supplier);
        return rows > 0 ? Result.success("添加成功") : Result.fail("添加失败");
    }

    @UserAction("'修改供应商：'+ #supplier.name")
    public Result update(SysSupplier supplier) {
        if (supplierMapper.isNameExist(supplier.getName(), supplier.getId())) {
            return Result.fail("修改失败，供应商【" + supplier.getName() + "】已存在");
        }
        int rows = supplierMapper.update(supplier);
        return rows > 0 ? Result.success("修改成功") : Result.fail("修改失败，请刷新重试");
    }

    @UserAction("删除供应商：'+ #supplier.name")
    public Result del(SysSupplier supplier) {
        int rows = supplierMapper.del(supplier.getId());
        return rows > 0 ? Result.success("删除成功") : Result.fail("删除失败，请刷新重试");
    }
}

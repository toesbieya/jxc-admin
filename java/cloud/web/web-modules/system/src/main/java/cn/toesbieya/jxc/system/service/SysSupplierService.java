package cn.toesbieya.jxc.system.service;

import cn.toesbieya.jxc.common.model.entity.SysRegion;
import cn.toesbieya.jxc.common.model.entity.SysSupplier;
import cn.toesbieya.jxc.common.model.vo.Result;
import cn.toesbieya.jxc.common.utils.Util;
import cn.toesbieya.jxc.system.mapper.SysRegionMapper;
import cn.toesbieya.jxc.system.mapper.SysSupplierMapper;
import cn.toesbieya.jxc.system.model.vo.RegionValueResult;
import cn.toesbieya.jxc.system.model.vo.SupplierSearch;
import cn.toesbieya.jxc.system.model.vo.SupplierVo;
import cn.toesbieya.jxc.web.common.annoation.UserAction;
import cn.toesbieya.jxc.web.common.model.vo.PageResult;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

@Service
public class SysSupplierService {
    @Resource
    private SysSupplierMapper supplierMapper;
    @Resource
    private SysRegionMapper regionMapper;

    public List<RegionValueResult> getLimitRegion() {
        return supplierMapper.getLimitRegion();
    }

    public PageResult<SupplierVo> search(SupplierSearch vo) {
        Integer id = vo.getId();
        String name = vo.getName();
        String address = vo.getAddress();
        String linkman = vo.getLinkman();
        String linkphone = vo.getLinkphone();
        String region = vo.getRegion();
        Integer status = vo.getStatus();
        Long startTime = vo.getStartTime();
        Long endTime = vo.getEndTime();

        Wrapper<SysSupplier> wrapper =
                Wrappers.lambdaQuery(SysSupplier.class)
                        .eq(id != null, SysSupplier::getId, id)
                        .like(!StringUtils.isEmpty(name), SysSupplier::getName, name)
                        .like(!StringUtils.isEmpty(address), SysSupplier::getAddress, address)
                        .like(!StringUtils.isEmpty(linkman), SysSupplier::getLinkman, linkman)
                        .eq(!StringUtils.isEmpty(linkphone), SysSupplier::getLinkphone, linkphone)
                        .inSql(!StringUtils.isEmpty(region), SysSupplier::getRegion, region)
                        .eq(status != null, SysSupplier::getStatus, status)
                        .ge(startTime != null, SysSupplier::getCtime, startTime)
                        .le(endTime != null, SysSupplier::getCtime, endTime)
                        .orderByDesc(SysSupplier::getCtime);

        PageHelper.startPage(vo.getPage(), vo.getPageSize());

        List<SysSupplier> suppliers = supplierMapper.selectList(wrapper);

        PageResult<SysSupplier> pageResult = new PageResult<>(suppliers);

        int supplierNum = suppliers.size();

        List<SupplierVo> list = new ArrayList<>(supplierNum);

        Set<String> regionIds = new HashSet<>(supplierNum);

        suppliers.forEach(supplier -> {
            list.add(new SupplierVo(supplier));
            regionIds.add(supplier.getRegion());
        });

        //获取关联的行政区域的名称
        List<SysRegion> regions = regionIds.size() > 0 ? regionMapper.selectBatchIds(regionIds) : Collections.emptyList();
        list.forEach(supplierVo -> {
            SysRegion matched = Util.find(regions, item -> supplierVo.getRegion().equals(item.getId()));
            if (matched != null) {
                supplierVo.setRegion_name(matched.getName());
            }
        });

        return new PageResult<>(pageResult.getTotal(), list);
    }

    @UserAction("'添加供应商：'+ #supplier.name")
    public Result add(SysSupplier supplier) {
        if (isNameExist(supplier.getName(), null)) {
            return Result.fail("添加失败，供应商【" + supplier.getName() + "】已存在");
        }

        int rows = supplierMapper.insert(supplier);
        return rows > 0 ? Result.success("添加成功") : Result.fail("添加失败");
    }

    @UserAction("'修改供应商：'+ #supplier.name")
    public Result update(SysSupplier supplier) {
        Integer id = supplier.getId();
        String name = supplier.getName();

        if (isNameExist(name, id)) {
            return Result.fail("修改失败，供应商【" + name + "】已存在");
        }

        int rows = supplierMapper.update(
                null,
                Wrappers.lambdaUpdate(SysSupplier.class)
                        .set(SysSupplier::getName, name)
                        .set(SysSupplier::getAddress, supplier.getAddress())
                        .set(SysSupplier::getLinkman, supplier.getLinkman())
                        .set(SysSupplier::getLinkphone, supplier.getLinkphone())
                        .set(SysSupplier::getRegion, supplier.getRegion())
                        .set(SysSupplier::getStatus, supplier.getStatus())
                        .set(SysSupplier::getRemark, supplier.getRemark())
                        .eq(SysSupplier::getId, id)
        );
        return rows > 0 ? Result.success("修改成功") : Result.fail("修改失败，请刷新重试");
    }

    @UserAction("删除供应商：'+ #supplier.name")
    public Result del(SysSupplier supplier) {
        int rows = supplierMapper.delete(
                Wrappers.lambdaQuery(SysSupplier.class)
                        .eq(SysSupplier::getId, supplier.getId())
                        .eq(SysSupplier::getStatus, 0)
        );
        return rows > 0 ? Result.success("删除成功") : Result.fail("删除失败，请刷新重试");
    }

    public boolean isNameExist(String name, Integer id) {
        Integer num = supplierMapper.selectCount(
                Wrappers.lambdaQuery(SysSupplier.class)
                        .eq(SysSupplier::getName, name)
                        .ne(id != null, SysSupplier::getId, id)
        );

        return num != null && num > 0;
    }
}

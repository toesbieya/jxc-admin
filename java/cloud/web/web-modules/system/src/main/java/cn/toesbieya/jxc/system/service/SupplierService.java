package cn.toesbieya.jxc.system.service;

import cn.toesbieya.jxc.common.model.entity.SysRegion;
import cn.toesbieya.jxc.common.model.entity.SysSupplier;
import cn.toesbieya.jxc.common.model.vo.R;
import cn.toesbieya.jxc.common.util.Util;
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
public class SupplierService {
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
        Boolean enable = vo.getEnable();
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
                        .eq(enable != null, SysSupplier::isEnable, enable)
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
        List<SysRegion> regions = regionIds.isEmpty() ? Collections.emptyList() : regionMapper.selectBatchIds(regionIds);
        list.forEach(supplierVo -> {
            SysRegion matched = Util.find(regions, item -> supplierVo.getRegion().equals(item.getId()));
            if (matched != null) {
                supplierVo.setRegionName(matched.getName());
            }
        });

        return new PageResult<>(pageResult.getTotal(), list);
    }

    @UserAction("'添加供应商：'+ #supplier.name")
    public R add(SysSupplier supplier) {
        if (isNameExist(supplier.getName(), null)) {
            return R.fail(String.format("添加失败，供应商【%s】已存在", supplier.getName()));
        }
        int rows = supplierMapper.insert(supplier);
        return rows > 0 ? R.success("添加成功") : R.fail("添加失败");
    }

    @UserAction("'修改供应商：'+ #supplier.name")
    public R update(SysSupplier supplier) {
        Integer id = supplier.getId();
        String name = supplier.getName();

        if (isNameExist(name, id)) {
            return R.fail(String.format("修改失败，供应商【%s】已存在", name));
        }

        supplierMapper.update(
                null,
                Wrappers.lambdaUpdate(SysSupplier.class)
                        .set(SysSupplier::getName, name)
                        .set(SysSupplier::getAddress, supplier.getAddress())
                        .set(SysSupplier::getLinkman, supplier.getLinkman())
                        .set(SysSupplier::getLinkphone, supplier.getLinkphone())
                        .set(SysSupplier::getRegion, supplier.getRegion())
                        .set(SysSupplier::isEnable, supplier.isEnable())
                        .set(SysSupplier::getRemark, supplier.getRemark())
                        .eq(SysSupplier::getId, id)
        );
        return R.success("修改成功");
    }

    @UserAction("删除供应商：'+ #supplier.name")
    public R del(SysSupplier supplier) {
        int rows = supplierMapper.delete(
                Wrappers.lambdaQuery(SysSupplier.class)
                        .eq(SysSupplier::getId, supplier.getId())
                        .eq(SysSupplier::isEnable, false)
        );
        return rows > 0 ? R.success("删除成功") : R.fail("删除失败，请刷新重试");
    }

    private boolean isNameExist(String name, Integer id) {
        Integer num = supplierMapper.selectCount(
                Wrappers.lambdaQuery(SysSupplier.class)
                        .eq(SysSupplier::getName, name)
                        .ne(id != null, SysSupplier::getId, id)
        );

        return num != null && num > 0;
    }
}

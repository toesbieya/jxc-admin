package cn.toesbieya.jxc.service.sys;

import cn.toesbieya.jxc.annoation.UserAction;
import cn.toesbieya.jxc.enumeration.GeneralStatusEnum;
import cn.toesbieya.jxc.mapper.SysCustomerMapper;
import cn.toesbieya.jxc.mapper.SysRegionMapper;
import cn.toesbieya.jxc.model.entity.SysCustomer;
import cn.toesbieya.jxc.model.entity.SysRegion;
import cn.toesbieya.jxc.model.vo.CustomerVo;
import cn.toesbieya.jxc.model.vo.Result;
import cn.toesbieya.jxc.model.vo.result.PageResult;
import cn.toesbieya.jxc.model.vo.result.RegionValueResult;
import cn.toesbieya.jxc.model.vo.search.CustomerSearch;
import cn.toesbieya.jxc.utils.Util;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

@Service
public class SysCustomerService {
    @Resource
    private SysCustomerMapper customerMapper;
    @Resource
    private SysRegionMapper regionMapper;

    public List<RegionValueResult> getLimitRegion() {
        return customerMapper.getLimitRegion();
    }

    public PageResult<CustomerVo> search(CustomerSearch vo) {
        Integer id = vo.getId();
        String name = vo.getName();
        String address = vo.getAddress();
        String linkman = vo.getLinkman();
        String linkphone = vo.getLinkphone();
        String region = vo.getRegion();
        Integer status = vo.getStatus();
        Long startTime = vo.getStartTime();
        Long endTime = vo.getEndTime();

        Wrapper<SysCustomer> wrapper =
                Wrappers.lambdaQuery(SysCustomer.class)
                        .eq(id != null, SysCustomer::getId, id)
                        .like(!StringUtils.isEmpty(name), SysCustomer::getName, name)
                        .like(!StringUtils.isEmpty(address), SysCustomer::getAddress, address)
                        .like(!StringUtils.isEmpty(linkman), SysCustomer::getLinkman, linkman)
                        .eq(!StringUtils.isEmpty(linkphone), SysCustomer::getLinkphone, linkphone)
                        .inSql(!StringUtils.isEmpty(region), SysCustomer::getRegion, region)
                        .eq(status != null, SysCustomer::getStatus, status)
                        .ge(startTime != null, SysCustomer::getCtime, startTime)
                        .le(endTime != null, SysCustomer::getCtime, endTime)
                        .orderByDesc(SysCustomer::getCtime);

        PageHelper.startPage(vo.getPage(), vo.getPageSize());

        List<SysCustomer> customers = customerMapper.selectList(wrapper);

        PageResult<SysCustomer> pageResult = new PageResult<>(customers);

        int customerNum = customers.size();

        List<CustomerVo> list = new ArrayList<>(customerNum);

        Set<String> regionIds = new HashSet<>(customerNum);

        customers.forEach(customer -> {
            list.add(new CustomerVo(customer));
            regionIds.add(customer.getRegion());
        });

        //获取关联的行政区域的名称
        List<SysRegion> regions = regionIds.size() > 0 ? regionMapper.selectBatchIds(regionIds) : Collections.emptyList();
        list.forEach(customerVo -> {
            SysRegion matched = Util.find(regions, item -> customerVo.getRegion().equals(item.getId()));

            if (matched != null) {
                customerVo.setRegionName(matched.getName());
            }
        });

        return new PageResult<>(pageResult.getTotal(), list);
    }

    @UserAction("'添加客户：'+ #customer.name")
    public Result add(SysCustomer customer) {
        if (isNameExist(customer.getName(), null)) {
            return Result.fail(String.format("添加失败，客户【%s】已存在", customer.getName()));
        }
        int rows = customerMapper.insert(customer);
        return rows > 0 ? Result.success("添加成功") : Result.fail("添加失败");
    }

    @UserAction("'修改客户：'+ #customer.name")
    public Result update(SysCustomer customer) {
        Integer id = customer.getId();
        String name = customer.getName();

        if (isNameExist(name, id)) {
            return Result.fail(String.format("修改失败，客户【%s】已存在", name));
        }

        int rows = customerMapper.update(
                null,
                Wrappers.lambdaUpdate(SysCustomer.class)
                        .set(SysCustomer::getName, name)
                        .set(SysCustomer::getAddress, customer.getAddress())
                        .set(SysCustomer::getLinkman, customer.getLinkman())
                        .set(SysCustomer::getLinkphone, customer.getLinkphone())
                        .set(SysCustomer::getRegion, customer.getRegion())
                        .set(SysCustomer::getStatus, customer.getStatus())
                        .set(SysCustomer::getRemark, customer.getRemark())
                        .eq(SysCustomer::getId, id)
        );
        return rows > 0 ? Result.success("修改成功") : Result.fail("修改失败，请刷新重试");
    }

    @UserAction("删除客户：'+ #customer.name")
    public Result del(SysCustomer customer) {
        int rows = customerMapper.delete(
                Wrappers.lambdaQuery(SysCustomer.class)
                        .eq(SysCustomer::getId, customer.getId())
                        .eq(SysCustomer::getStatus, GeneralStatusEnum.DISABLED.getCode())
        );
        return rows > 0 ? Result.success("删除成功") : Result.fail("删除失败，请刷新重试");
    }

    private boolean isNameExist(String name, Integer id) {
        Integer num = customerMapper.selectCount(
                Wrappers.lambdaQuery(SysCustomer.class)
                        .eq(SysCustomer::getName, name)
                        .ne(id != null, SysCustomer::getId, id)
        );

        return num != null && num > 0;
    }
}

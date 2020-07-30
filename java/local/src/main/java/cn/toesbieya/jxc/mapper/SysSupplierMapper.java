package cn.toesbieya.jxc.mapper;

import cn.toesbieya.jxc.model.entity.SysSupplier;
import cn.toesbieya.jxc.model.vo.result.RegionValueResult;
import cn.toesbieya.jxc.model.vo.search.SupplierSearch;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysSupplierMapper {
    List<SysSupplier> get();

    SysSupplier getById(int id);

    List<SysSupplier> search(SupplierSearch vo);

    int add(SysSupplier category);

    int update(SysSupplier category);

    int del(int id);

    List<RegionValueResult> getLimitRegion();

    boolean isNameExist(@Param("name") String name, @Param("id") Integer id);
}

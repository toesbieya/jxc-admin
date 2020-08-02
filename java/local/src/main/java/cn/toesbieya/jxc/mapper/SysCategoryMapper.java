package cn.toesbieya.jxc.mapper;

import cn.toesbieya.jxc.model.entity.SysCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

public interface SysCategoryMapper extends BaseMapper<SysCategory> {
    boolean checkIsUse(@Param("cid") Integer cid);
}

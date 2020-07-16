package cn.toesbieya.jxc.system.mapper;

import cn.toesbieya.jxc.common.model.entity.SysCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

public interface SysCategoryMapper extends BaseMapper<SysCategory> {
    boolean checkIsUse(@Param("cid") Integer cid);
}

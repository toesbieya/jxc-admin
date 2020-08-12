package cn.toesbieya.jxc.mapper;

import cn.toesbieya.jxc.model.entity.SysResource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysResourceMapper extends BaseMapper<SysResource> {
    List<SysResource> selectChildren(@Param("ids") String ids);
}

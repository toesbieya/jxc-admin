package cn.toesbieya.jxc.api.service.system;

import cn.toesbieya.jxc.common.model.entity.SysRole;
import cn.toesbieya.jxc.common.model.vo.ResourceVo;

import java.util.List;

public interface ResourceApi {
    //获取角色的权限列表，包含admin权限
    List<ResourceVo> getResourceByRole(SysRole role);
}

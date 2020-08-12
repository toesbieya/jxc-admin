package cn.toesbieya.jxc.api.system;

import cn.toesbieya.jxc.common.model.entity.SysResource;
import cn.toesbieya.jxc.common.model.entity.SysRole;

import java.util.List;

public interface ResourceApi {
    //获取已启用的数据接口列表
    List<SysResource> getEnableApi();

    //获取角色的权限列表，包含admin权限
    List<SysResource> getResourceByRole(SysRole role);
}

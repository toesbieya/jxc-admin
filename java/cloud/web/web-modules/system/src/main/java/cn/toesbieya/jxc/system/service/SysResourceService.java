package cn.toesbieya.jxc.system.service;

import cn.toesbieya.jxc.api.service.system.ResourceApi;
import cn.toesbieya.jxc.common.model.entity.SysResource;
import cn.toesbieya.jxc.common.model.entity.SysRole;
import cn.toesbieya.jxc.system.mapper.SysResourceMapper;
import cn.toesbieya.jxc.common.model.vo.ResourceVo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service
@org.apache.dubbo.config.annotation.Service
public class SysResourceService implements ResourceApi {
    @Resource
    private SysResourceMapper resourceMapper;

    public List<ResourceVo> get() {
        List<SysResource> list = resourceMapper.selectList(
                Wrappers.lambdaQuery(SysResource.class)
                        .eq(SysResource::getAdmin, 0)
        );

        return completeNode(list);
    }

    public List<ResourceVo> getAll() {
        List<SysResource> list = resourceMapper.selectList(null);
        return completeNode(list);
    }

    @Override
    public List<ResourceVo> getResourceByRole(SysRole role) {
        if (role == null) {
            return Collections.emptyList();
        }

        String ids = role.getResource_id();

        if (StringUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }

        List<SysResource> list = resourceMapper.selectList(
                Wrappers.lambdaQuery(SysResource.class)
                        .inSql(SysResource::getId, ids)
        );

        return this.completeNode(list);
    }

    private List<ResourceVo> completeNode(List<SysResource> list) {
        List<ResourceVo> result = new ArrayList<>(list.size());
        HashMap<Integer, String> urlMap = new HashMap<>(128);
        HashMap<Integer, String> nameMap = new HashMap<>(128);

        for (SysResource resource : list) {
            ResourceVo vo = new ResourceVo(resource);

            Integer id = resource.getId();
            String url = resource.getUrl();
            String name = resource.getName();

            //跳过顶级节点
            if (resource.getPid() == 0) {
                vo.setFullName(name);

                urlMap.put(id, url);
                nameMap.put(id, name);
            }
            else {
                Integer pid = resource.getPid();

                //获取父节点进行拼接
                String parentUrl = urlMap.get(pid);
                String parentName = nameMap.get(pid);

                assert !StringUtils.isEmpty(parentUrl) && !StringUtils.isEmpty(parentName);

                String fullName = parentName + " - " + name;

                vo.setUrl(parentUrl + url);
                vo.setFullName(fullName);

                urlMap.put(id, url);
                nameMap.put(id, fullName);
            }

            result.add(vo);
        }

        return result;
    }
}

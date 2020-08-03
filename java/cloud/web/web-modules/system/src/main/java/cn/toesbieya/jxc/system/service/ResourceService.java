package cn.toesbieya.jxc.system.service;

import cn.toesbieya.jxc.api.system.ResourceApi;
import cn.toesbieya.jxc.common.model.entity.SysResource;
import cn.toesbieya.jxc.common.model.entity.SysRole;
import cn.toesbieya.jxc.common.model.vo.ResourceVo;
import cn.toesbieya.jxc.common.model.vo.Result;
import cn.toesbieya.jxc.system.mapper.SysResourceMapper;
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
public class ResourceService implements ResourceApi {
    @Resource
    private SysResourceMapper mapper;

    public List<ResourceVo> getAll() {
        return completeNode(mapper.selectList(null));
    }

    @Override
    public List<ResourceVo> getResourceByRole(SysRole role) {
        if (role == null) {
            return Collections.emptyList();
        }

        String ids = role.getResourceId();

        if (StringUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }

        List<SysResource> list = mapper.selectList(
                Wrappers.lambdaQuery(SysResource.class)
                        .inSql(SysResource::getId, ids)
        );

        return completeNode(list);
    }

    public Result update(SysResource resource) {
        int rows = mapper.updateById(resource);
        return Result.success("修改成功");
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
                vo.setFullname(name);

                urlMap.put(id, url);
                nameMap.put(id, name);
            }
            else {
                Integer pid = resource.getPid();

                //获取父节点进行拼接
                String parentUrl = urlMap.get(pid);
                String parentName = nameMap.get(pid);

                assert !StringUtils.isEmpty(parentUrl) && !StringUtils.isEmpty(parentName);

                String fullname = parentName + " - " + name;

                vo.setUrl(parentUrl + url);
                vo.setFullname(fullname);

                urlMap.put(id, url);
                nameMap.put(id, fullname);
            }

            result.add(vo);
        }

        return result;
    }
}

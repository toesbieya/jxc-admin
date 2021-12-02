package cn.toesbieya.jxc.system.service;

import cn.toesbieya.jxc.api.system.ResourceApi;
import cn.toesbieya.jxc.common.enumeration.ResourceTypeEnum;
import cn.toesbieya.jxc.common.model.entity.SysResource;
import cn.toesbieya.jxc.common.model.entity.SysRole;
import cn.toesbieya.jxc.common.model.vo.R;
import cn.toesbieya.jxc.system.mapper.SysResourceMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@org.apache.dubbo.config.annotation.Service
public class ResourceService implements ResourceApi {
    @Resource
    private SysResourceMapper mapper;

    public List<SysResource> getAll() {
        return mapper.selectList(null);
    }

    @Override
    public List<SysResource> getEnableApi() {
        return getEnableResourceTree(null, i -> i.getType().equals(ResourceTypeEnum.API.getCode()));
    }

    @Override
    public List<SysResource> getResourceByRole(SysRole role) {
        if (role == null) {
            return Collections.emptyList();
        }

        String ids = role.getResourceId();

        if (StringUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }

        return getEnableResourceTree(ids, i -> {
            Integer type = i.getType();
            return !type.equals(ResourceTypeEnum.ROOT.getCode())
                    && !type.equals(ResourceTypeEnum.FOLDER.getCode());
        });
    }

    public R add(SysResource entity) {
        String error = check(entity);
        if (!StringUtils.isEmpty(error)) {
            return R.fail("添加失败，" + error);
        }

        entity.setId(null);
        transform(entity);

        mapper.insert(entity);

        return R.success("添加成功");
    }

    public R update(SysResource entity) {
        String error = check(entity);
        if (!StringUtils.isEmpty(error)) {
            return R.fail("修改失败，" + error);
        }

        transform(entity);

        mapper.updateById(entity);

        return R.success("修改成功");
    }

    public R del(int id) {
        Integer childrenNum = mapper.selectCount(
                Wrappers.lambdaQuery(SysResource.class)
                        .eq(SysResource::getPid, id)
        );
        if (childrenNum != null && childrenNum > 0) {
            return R.fail("删除失败，该节点下已有其他子节点存在");
        }
        int row = mapper.deleteById(id);
        return row > 0 ? R.success("删除成功") : R.fail("删除失败");
    }

    /**
     * 获取路径经过叠加且以启用的resource列表
     * @param ids       resource的id集合，逗号隔开，传入null则返回全部
     * @param predicate 额外的过滤条件，传入null则不进行额外判断
     */
    private List<SysResource> getEnableResourceTree(String ids, Predicate<SysResource> predicate) {
        return mapper
                .selectChildren(ids)
                .stream()
                .filter(r -> {
                    if (!r.isEnable() || predicate != null && !predicate.test(r)) {
                        return false;
                    }

                    String path = r.getPath();
                    int index = path.lastIndexOf("//");
                    if (index != -1 && !path.startsWith("http")) {
                        //去掉'//'前拼接的父节点的path
                        r.setPath(path.substring(index + 1));
                    }

                    return true;
                })
                .collect(Collectors.toList());
    }

    //判断节点是否合法，不合法则返回错误信息
    private String check(SysResource entity) {
        Integer pid = entity.getPid();
        Integer type = entity.getType();

        //顶部菜单的pid必须为0
        if (type.equals(ResourceTypeEnum.ROOT.getCode())) {
            return pid.equals(0) ? null : "顶部菜单不能有父节点";
        }

        //校验父节点
        SysResource parent = mapper.selectById(pid);
        if (parent == null) {
            return "父节点不存在，请刷新后重试";
        }
        Integer parentType = parent.getType();
        //1.待添加的节点为数据接口且父节点必须为叶子菜单
        boolean isApi = type.equals(ResourceTypeEnum.API.getCode());
        if (isApi) {
            if (!parentType.equals(ResourceTypeEnum.LEAF.getCode())) {
                return "数据接口只能以页面菜单为父节点";
            }
        }
        //2.非数据接口的节点，其父节点必须为聚合菜单
        else if (!parentType.equals(ResourceTypeEnum.ROOT.getCode()) && !parentType.equals(ResourceTypeEnum.FOLDER.getCode())) {
            return "菜单只能以聚合菜单或顶部菜单为父节点";
        }
        return null;
    }

    //数据转换
    private void transform(SysResource entity) {
        Integer type = entity.getType();
        boolean isRoot = type.equals(ResourceTypeEnum.ROOT.getCode());
        boolean isFolder = type.equals(ResourceTypeEnum.FOLDER.getCode());
        boolean isLeaf = type.equals(ResourceTypeEnum.LEAF.getCode());
        boolean isApi = type.equals(ResourceTypeEnum.API.getCode());

        //能自定义component的只有叶子菜单
        if (!isLeaf) {
            //顶部菜单的组件只能为'Layout'
            entity.setComponent(isRoot ? "Layout" : null);
        }
        //顶部或聚合菜单不能设置name（此处的name是路由名称）
        if (isRoot || isFolder) {
            entity.setName(null);
        }
        //数据接口不能设置meta
        if (isApi) {
            entity.setMeta(null);
        }
    }
}

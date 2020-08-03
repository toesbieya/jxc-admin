package cn.toesbieya.jxc.account.service;

import cn.toesbieya.jxc.account.enumeration.RecLoginHistoryEnum;
import cn.toesbieya.jxc.account.mapper.SysUserMapper;
import cn.toesbieya.jxc.account.vo.LoginParam;
import cn.toesbieya.jxc.account.vo.LoginSuccessInfo;
import cn.toesbieya.jxc.account.vo.PasswordUpdateParam;
import cn.toesbieya.jxc.account.vo.RegisterParam;
import cn.toesbieya.jxc.api.FileApi;
import cn.toesbieya.jxc.api.RecordApi;
import cn.toesbieya.jxc.api.system.DepartmentApi;
import cn.toesbieya.jxc.api.system.ResourceApi;
import cn.toesbieya.jxc.api.system.RoleApi;
import cn.toesbieya.jxc.common.enumeration.GeneralStatusEnum;
import cn.toesbieya.jxc.common.model.entity.RecLoginHistory;
import cn.toesbieya.jxc.common.model.entity.SysRole;
import cn.toesbieya.jxc.common.model.entity.SysUser;
import cn.toesbieya.jxc.common.model.vo.DepartmentVo;
import cn.toesbieya.jxc.common.model.vo.ResourceVo;
import cn.toesbieya.jxc.common.model.vo.Result;
import cn.toesbieya.jxc.common.model.vo.UserVo;
import cn.toesbieya.jxc.common.util.SessionUtil;
import cn.toesbieya.jxc.web.common.annoation.TimeCost;
import cn.toesbieya.jxc.web.common.annoation.UserAction;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

@Service
@Slf4j
public class AccountService {
    @Resource
    private SysUserMapper userMapper;
    @Reference
    private FileApi fileApi;
    @Reference
    private RecordApi recordApi;
    @Reference
    private ResourceApi resourceApi;
    @Reference
    private RoleApi roleApi;
    @Reference
    public DepartmentApi departmentApi;

    @TimeCost
    public Result login(LoginParam param, String ip) {
        long now = System.currentTimeMillis();

        SysUser user = userMapper.selectOne(
                Wrappers.lambdaQuery(SysUser.class)
                        .eq(SysUser::getName, param.getUsername())
                        .eq(SysUser::getPwd, param.getPassword())
        );

        if (user == null) {
            return Result.fail("用户名或密码错误");
        }
        if (user.getStatus() == GeneralStatusEnum.DISABLED.getCode()) {
            return Result.fail("该用户已被禁用，请联系管理员");
        }
        Integer roleId = user.getRole();
        if (!user.isAdmin() && roleId == null) {
            return Result.fail("该用户尚未被分配角色，请联系管理员");
        }

        //设置token
        String token = SessionUtil.generateToken(user);

        //存入redis的数据
        UserVo userVo = new UserVo(user);
        userVo.setToken(token);

        //传递给前端的数据
        LoginSuccessInfo info = new LoginSuccessInfo(user);
        info.setToken(token);

        Map<String, Integer> userResourcesUrlMap = null;
        Set<Integer> userResourcesIdSet = null;

        if (roleId != null) {
            //获取用户的角色
            SysRole role = roleApi.getRoleById(roleId);

            if (role != null) {
                //设置角色名称
                String roleName = role.getName();
                userVo.setRoleName(roleName);
                info.setRoleName(roleName);

                //获取用户的权限列表
                List<ResourceVo> resources = resourceApi.getResourceByRole(role);

                userResourcesUrlMap = new HashMap<>(128);
                userResourcesIdSet = new HashSet<>(128);

                for (ResourceVo resource : resources) {
                    Integer resourceId = resource.getId();
                    userResourcesUrlMap.put(resource.getUrl(), resourceId);
                    userResourcesIdSet.add(resourceId);
                }
            }
        }

        //获取用户部门
        Integer deptId = user.getDept();
        if (deptId != null) {
            DepartmentVo dept = departmentApi.getById(deptId);
            if (dept != null) {
                //设置用户的部门名称
                String deptName = dept.getFullname();
                userVo.setDeptName(deptName);
                info.setDeptName(deptName);
            }
        }

        //用户的数据范围
        userVo.setDepartmentIds(departmentApi.getUserDataScope(user));

        //用户的权限ID集合
        userVo.setResourceIds(userResourcesIdSet);

        //返回给前端的用户权限表
        info.setResources(userResourcesUrlMap);

        //用户信息插入redis
        SessionUtil.save(userVo);

        //记录登陆信息
        recordApi.insertLoginHistory(
                RecLoginHistory
                        .builder()
                        .uid(user.getId())
                        .uname(user.getName())
                        .ip(ip)
                        .type(RecLoginHistoryEnum.LOGIN.getCode())
                        .time(now)
                        .build()
        );

        return Result.success(info);
    }

    public Result logout(UserVo user, String ip) {
        if (user != null) {
            recordApi.insertLoginHistory(
                    RecLoginHistory
                            .builder()
                            .uid(user.getId())
                            .uname(user.getName())
                            .ip(ip)
                            .type(RecLoginHistoryEnum.LOGOUT.getCode())
                            .time(System.currentTimeMillis())
                            .build()
            );
            SessionUtil.remove(user.getToken());
        }

        return Result.success("登出成功");
    }

    public Result register(RegisterParam param) {
        String name = param.getUsername();

        if (checkName(name, null)) {
            return Result.fail("该用户名称已存在");
        }

        SysUser user = new SysUser();
        user.setName(name);
        user.setPwd(param.getPassword());
        user.setRole(1);
        user.setStatus(GeneralStatusEnum.ENABLED.getCode());
        user.setCtime(System.currentTimeMillis());

        userMapper.insert(user);

        return Result.success("注册成功");
    }

    @UserAction("'修改密码'")
    public Result updatePwd(PasswordUpdateParam param) {
        int rows = userMapper.update(
                null,
                Wrappers.lambdaUpdate(SysUser.class)
                        .set(SysUser::getPwd, param.getNewPwd())
                        .eq(SysUser::getId, param.getId())
                        .eq(SysUser::getPwd, param.getOldPwd())
        );
        return rows > 0 ? Result.success("修改成功") : Result.fail("修改失败，请检查原密码是否正确");
    }

    @UserAction("'修改头像'")
    public Result updateAvatar(UserVo user, String avatar) {
        int rows = userMapper.update(
                null,
                Wrappers.lambdaUpdate(SysUser.class)
                        .set(SysUser::getAvatar, avatar)
                        .eq(SysUser::getId, user.getId())
        );

        //更新成功后删除云上旧的头像，同时更新redis中的用户信息
        if (rows > 0) {
            String oldAvatar = user.getAvatar();
            if (!StringUtils.isEmpty(oldAvatar)) {
                fileApi.delete(oldAvatar);
            }
            user.setAvatar(avatar);
            SessionUtil.save(user);
            return Result.success("上传头像成功");
        }

        //否则删除此次上传至云的头像
        fileApi.delete(avatar);

        return Result.fail("上传头像失败");
    }

    //用户名重复时返回true
    public boolean checkName(String name, Integer id) {
        Integer num = userMapper.selectCount(
                Wrappers.lambdaQuery(SysUser.class)
                        .eq(SysUser::getName, name)
                        .ne(id != null, SysUser::getId, id)
        );
        return num == null || num.equals(0);
    }
}

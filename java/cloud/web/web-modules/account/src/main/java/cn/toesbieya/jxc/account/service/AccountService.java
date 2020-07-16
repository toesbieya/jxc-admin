package cn.toesbieya.jxc.account.service;

import cn.toesbieya.jxc.account.enumeration.RecLoginHistoryEnum;
import cn.toesbieya.jxc.account.mapper.SysUserMapper;
import cn.toesbieya.jxc.account.vo.LoginParam;
import cn.toesbieya.jxc.account.vo.LoginSuccessInfo;
import cn.toesbieya.jxc.account.vo.PasswordUpdateParam;
import cn.toesbieya.jxc.account.vo.RegisterParam;
import cn.toesbieya.jxc.api.service.RecordApi;
import cn.toesbieya.jxc.api.service.system.ResourceApi;
import cn.toesbieya.jxc.api.service.system.RoleApi;
import cn.toesbieya.jxc.common.enumeration.GeneralStatusEnum;
import cn.toesbieya.jxc.common.model.entity.RecLoginHistory;
import cn.toesbieya.jxc.common.model.entity.SysRole;
import cn.toesbieya.jxc.common.model.entity.SysUser;
import cn.toesbieya.jxc.common.model.vo.ResourceVo;
import cn.toesbieya.jxc.common.model.vo.Result;
import cn.toesbieya.jxc.common.model.vo.UserVo;
import cn.toesbieya.jxc.common.utils.SessionUtil;
import cn.toesbieya.jxc.web.common.annoation.TimeCost;
import cn.toesbieya.jxc.web.common.annoation.UserAction;
import cn.toesbieya.jxc.web.common.utils.QiniuUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
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
    @Resource
    private QiniuUtil qiniuUtil;
    @Reference
    private RecordApi recordApi;
    @Reference
    private ResourceApi resourceApi;
    @Reference
    private RoleApi roleApi;

    @TimeCost
    public Result login(LoginParam param, String ip) {
        Wrapper<SysUser> wrapper =
                Wrappers.lambdaQuery(SysUser.class)
                        .eq(SysUser::getName, param.getUsername())
                        .eq(SysUser::getPwd, param.getPassword());

        SysUser user = userMapper.selectOne(wrapper);

        if (user == null) {
            return Result.fail("用户名或密码错误");
        }
        if (user.getStatus() == GeneralStatusEnum.DISABLED.getCode()) {
            return Result.fail("该用户已被禁用，请联系管理员");
        }
        Integer roleId = user.getRole();
        if (user.getAdmin() != 1 && roleId == null) {
            return Result.fail("该用户尚未被分配角色，请联系管理员");
        }

        String token = SessionUtil.generateToken(user);

        //存入redis的数据
        UserVo userVo = new UserVo(user);
        userVo.setToken(token);

        //传递给前端的数据
        LoginSuccessInfo info = new LoginSuccessInfo(user);
        info.setToken(token);

        Map<String, Integer> userResourcesUrlMap = null;

        Set<Integer> userResourcesIdSet = null;

        //获取用户的所有权限url
        if (roleId != null) {
            //获取用户的角色
            SysRole role = roleApi.getRoleById(roleId);

            if (role != null) {
                String role_name = role.getName();
                userVo.setRole_name(role_name);
                info.setRole_name(role_name);

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

        userVo.setResource_ids(userResourcesIdSet);

        info.setResources(userResourcesUrlMap);

        //用户信息插入redis
        SessionUtil.save(userVo);

        //记录登陆信息
        recordApi.insertLoginHistory(
                RecLoginHistory.builder()
                        .uid(user.getId())
                        .uname(user.getName())
                        .ip(ip)
                        .type(RecLoginHistoryEnum.LOGIN.getCode())
                        .time(System.currentTimeMillis())
                        .build()
        );

        return Result.success(info);
    }

    public Result logout(UserVo user, String ip) {
        if (user != null) {
            recordApi.insertLoginHistory(
                    RecLoginHistory.builder()
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

        if (0 != userMapper.selectCount(Wrappers.lambdaQuery(SysUser.class).eq(SysUser::getName, name))) {
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
                        .set(SysUser::getPwd, param.getNew_pwd())
                        .eq(SysUser::getId, param.getId())
                        .eq(SysUser::getPwd, param.getOld_pwd())
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
                qiniuUtil.delete(oldAvatar);
            }

            user.setAvatar(avatar);
            SessionUtil.save(user);

            return Result.success("上传头像成功");
        }

        //否则删除此次上传至云的头像
        qiniuUtil.delete(avatar);

        return Result.fail("上传头像失败");
    }

    public boolean checkName(String name, Integer id) {
        return 0 == userMapper.selectCount(
                Wrappers.lambdaQuery(SysUser.class)
                        .eq(SysUser::getName, name)
                        .ne(id != null, SysUser::getId, id)
        );
    }
}

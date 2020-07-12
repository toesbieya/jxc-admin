package com.toesbieya.my.service;

import com.toesbieya.my.annoation.UserAction;
import com.toesbieya.my.enumeration.GeneralStatusEnum;
import com.toesbieya.my.enumeration.RecLoginHistoryEnum;
import com.toesbieya.my.mapper.SysUserMapper;
import com.toesbieya.my.model.entity.SysResource;
import com.toesbieya.my.model.entity.SysUser;
import com.toesbieya.my.model.vo.*;
import com.toesbieya.my.utils.QiniuUtil;
import com.toesbieya.my.utils.Result;
import com.toesbieya.my.utils.SessionUtil;
import com.toesbieya.my.utils.Util;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

@Service
public class AccountService {
    @Resource
    private SysUserMapper userMapper;
    @Resource
    private SysResourceService resourceService;
    @Resource
    private RecService recService;

    public Result login(LoginParam param, String ip) {
        SysUser user = userMapper.getByNameAndPwd(param.getUsername(), param.getPassword());

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

        //设置token
        String token = Util.UUID();

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
            List<SysResource> resources = resourceService.getByRole(user.getRole());

            userResourcesUrlMap = new HashMap<>(128);
            userResourcesIdSet = new HashSet<>(128);

            for (SysResource resource : resources) {
                Integer resourceId = resource.getId();
                userResourcesUrlMap.put(resource.getUrl(), resourceId);
                userResourcesIdSet.add(resourceId);
            }
        }

        userVo.setResource_ids(userResourcesIdSet);

        info.setResources(userResourcesUrlMap);

        //用户信息插入redis
        SessionUtil.save(userVo);

        //记录登陆信息
        recService.insertLoginHistory(userVo, ip, RecLoginHistoryEnum.LOGIN);

        return Result.success(info);
    }

    public Result logout(UserVo user, String ip) {
        if (user != null) {
            recService.insertLoginHistory(user, ip, RecLoginHistoryEnum.LOGOUT);
            SessionUtil.remove(user.getToken());
        }

        return Result.success("登出成功");
    }

    public Result register(RegisterParam param) {
        String name = param.getUsername();

        if (userMapper.isNameExist(name, null)) {
            return Result.fail("该用户名称已存在");
        }

        SysUser user = new SysUser();
        user.setName(name);
        user.setPwd(param.getPassword());
        user.setRole(1);
        user.setStatus(GeneralStatusEnum.ENABLED.getCode());
        user.setCtime(System.currentTimeMillis());

        userMapper.add(user);

        return Result.success("注册成功");
    }

    @UserAction("'修改密码'")
    public Result updatePwd(PasswordUpdateParam param) {
        int rows = userMapper.updatePwd(param);
        return rows > 0 ? Result.success("修改成功") : Result.fail("修改失败，请检查原密码是否正确");
    }

    @UserAction("'修改头像'")
    public Result updateAvatar(UserVo user, String avatar) {
        int rows = userMapper.updateAvatar(user.getId(), avatar);

        //更新成功后删除云上旧的头像，同时更新redis中的用户信息
        if (rows > 0) {
            String oldAvatar = user.getAvatar();
            if (!StringUtils.isEmpty(oldAvatar)) {
                QiniuUtil.delete(oldAvatar);
            }

            user.setAvatar(avatar);
            SessionUtil.save(user);

            return Result.success("上传头像成功");
        }

        //否则删除此次上传至云的头像
        QiniuUtil.delete(avatar);

        return Result.fail("上传头像失败");
    }

    public boolean checkName(String name, Integer id) {
        return !userMapper.isNameExist(name, id);
    }
}

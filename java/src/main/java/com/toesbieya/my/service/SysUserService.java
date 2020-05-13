package com.toesbieya.my.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageSerializable;
import com.toesbieya.my.annoation.Tx;
import com.toesbieya.my.annoation.UserAction;
import com.toesbieya.my.constant.SessionConstant;
import com.toesbieya.my.enumeration.GeneralStatusEnum;
import com.toesbieya.my.enumeration.RecLoginHistoryEnum;
import com.toesbieya.my.mapper.SysUserMapper;
import com.toesbieya.my.model.entity.RecUserAction;
import com.toesbieya.my.model.entity.SysResource;
import com.toesbieya.my.model.entity.SysUser;
import com.toesbieya.my.model.vo.search.UserSearch;
import com.toesbieya.my.model.vo.update.UserUpdatePwd;
import com.toesbieya.my.module.SocketModule;
import com.toesbieya.my.utils.QiniuUtil;
import com.toesbieya.my.utils.Result;
import com.toesbieya.my.utils.ThreadUtil;
import com.toesbieya.my.utils.Util;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Service
public class SysUserService {
    private static final String DEFAULT_PWD = DigestUtils.md5DigestAsHex("123456".getBytes());
    @Resource
    private SysUserMapper userMapper;
    @Resource
    private SysResourceService resourceService;
    @Resource
    private RecService recService;

    public Result login(String name, String pwd, String ip) {
        HttpSession session = Util.getRequest().getSession();
        SysUser user = userMapper.getByNameAndPwd(name, pwd);

        if (user == null) {
            return Result.fail("用户名或密码错误");
        }
        else if (user.getStatus() == GeneralStatusEnum.DISABLED.getCode()) {
            return Result.fail("该用户已被禁用，请联系管理员");
        }
        else if (user.getAdmin() != 1 && (user.getRole() == null || StringUtils.isEmpty(user.getRole_name()))) {
            return Result.fail("该用户尚未被分配角色，请联系管理员");
        }

        //设置token
        String token = DigestUtils.md5DigestAsHex((user.getId() + "" + System.currentTimeMillis()).getBytes());

        //传输回前端
        HashMap<String, Object> userVo = new HashMap<>();

        List<SysResource> resources;

        HashMap<String, Integer> userResourcesUrlMap = new HashMap<>();

        HashSet<Integer> userResourcesIdSet = new HashSet<>();

        //获取用户的所有权限url
        if (user.getRole() != null) {
            resources = resourceService.getByRole(user.getRole());
            userResourcesUrlMap = new HashMap<>(128);
            userResourcesIdSet = new HashSet<>(128);
            for (SysResource p : resources) {
                userResourcesUrlMap.put(p.getUrl(), p.getId());
                userResourcesIdSet.add(p.getId());
            }
        }

        user.setResource_ids(userResourcesIdSet);

        userVo.put("id", user.getId());
        userVo.put("resources", userResourcesUrlMap);
        userVo.put("name", user.getName());
        userVo.put("avatar", user.getAvatar());
        userVo.put("admin", user.getAdmin());
        userVo.put("token", token);
        userVo.put("role_name", user.getRole_name());
        userVo.put("session_id", session.getId());

        recService.insertLoginHistory(user, ip, RecLoginHistoryEnum.LOGIN);

        session.setAttribute(SessionConstant.USER_KEY, user);
        session.setAttribute(SessionConstant.TOKEN_KEY, token);
        return Result.success(userVo);
    }

    //演示用的临时注册功能
    public Result register(String name, String pwd) {
        if (userMapper.isNameExist(name, null)) {
            return Result.fail("该用户名称已存在");
        }

        SysUser user = new SysUser();
        user.setName(name);
        user.setRole(1);
        user.setCtime(System.currentTimeMillis());
        user.setPwd(pwd);

        userMapper.add(user);
        return Result.success("注册成功");
    }

    public PageSerializable<SysUser> search(UserSearch vo) {
        PageHelper.startPage(vo.getPage(), vo.getPageSize());
        List<SysUser> users = userMapper.search(vo);
        for (SysUser user : users) {
            user.setOnline(SocketModule.online(user.getId()));
        }
        return new PageSerializable<>(users);
    }

    @UserAction("'添加用户：'+#user.name")
    public Result add(SysUser user) {
        if (userMapper.isNameExist(user.getName(), null)) {
            return Result.fail("该用户名称已存在");
        }

        user.setCtime(System.currentTimeMillis());
        user.setPwd(DEFAULT_PWD);

        userMapper.add(user);
        return Result.success("添加成功");
    }

    @UserAction
    public Result update(SysUser user) {
        if (userMapper.isNameExist(user.getName(), user.getId())) {
            return Result.fail("该用户名称已存在");
        }

        userMapper.update(user);

        return Result.success("修改成功");
    }

    @UserAction("'删除用户：'+#user.name")
    @Tx
    public Result del(SysUser user) {
        userMapper.del(user.getId());
        SocketModule.logout(user.getId(), "该用户已删除");
        return Result.success("删除成功");
    }

    @UserAction
    public Result kick(List<SysUser> users) {
        for (SysUser user : users) {
            SocketModule.logout(user.getId(), null);
        }
        return Result.success("踢出成功");
    }

    @UserAction("'重置用户密码：'+#user.name")
    public Result resetPwd(SysUser user) {
        int rows = userMapper.resetPwd(user.getId(), DEFAULT_PWD);
        return rows > 0 ? Result.success() : Result.fail("重置失败，未匹配到用户");
    }

    @UserAction("'修改密码'")
    public Result updatePwd(UserUpdatePwd vo) {
        int rows = userMapper.updatePwd(vo);
        return rows > 0 ? Result.success("修改成功") : Result.fail("修改失败，请检查原密码是否正确");
    }

    public Result updateAvatar(SysUser user, String key, HttpSession session) {
        int rows = userMapper.updateAvatar(user.getId(), key);
        if (rows > 0) {
            if (!StringUtils.isEmpty(user.getAvatar())) {
                QiniuUtil.delete(user.getAvatar());
            }
            user.setAvatar(key);
            session.setAttribute(SessionConstant.USER_KEY, user);
            return Result.success("上传头像成功");
        }
        QiniuUtil.delete(key);
        return Result.fail("上传头像失败");
    }

    public boolean checkName(String name) {
        return !userMapper.isNameExist(name, null);
    }

    public void setUpdateAction(SysUser user) {
        RecUserAction action = ThreadUtil.getAction();
        SysUser origin = userMapper.getById(user.getId());
        action.setAction(String.format("修改用户【%s】%s", origin.getName(), SysUser.getUpdateInfo(origin, user)));
    }
}

package cn.toesbieya.jxc.service;

import cn.toesbieya.jxc.annoation.TimeCost;
import cn.toesbieya.jxc.annoation.UserAction;
import cn.toesbieya.jxc.enumeration.ResourceTypeEnum;
import cn.toesbieya.jxc.mapper.SysUserMapper;
import cn.toesbieya.jxc.model.entity.RecLoginHistory;
import cn.toesbieya.jxc.model.entity.SysResource;
import cn.toesbieya.jxc.model.entity.SysRole;
import cn.toesbieya.jxc.model.entity.SysUser;
import cn.toesbieya.jxc.model.vo.*;
import cn.toesbieya.jxc.service.sys.SysDepartmentService;
import cn.toesbieya.jxc.service.sys.SysResourceService;
import cn.toesbieya.jxc.service.sys.SysRoleService;
import cn.toesbieya.jxc.util.SessionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
    @Resource
    private SysDepartmentService departmentService;
    @Resource
    private SysRoleService roleService;
    @Resource
    private FileService fileService;

    @TimeCost
    public R login(LoginParam param, String ip) {
        long now = System.currentTimeMillis();

        SysUser user = userMapper.selectOne(
                Wrappers.lambdaQuery(SysUser.class)
                        .eq(SysUser::getLoginName, param.getUsername())
                        .eq(SysUser::getPwd, param.getPassword())
        );

        if (user == null) {
            return R.fail("用户名或密码错误");
        }
        if (!user.isEnable()) {
            return R.fail("该用户已被禁用，请联系管理员");
        }
        Integer roleId = user.getRole();
        if (!user.isAdmin() && roleId == null) {
            return R.fail("该用户尚未被分配角色，请联系管理员");
        }

        //设置token
        String token = SessionUtil.generateToken(user);

        //存入redis的数据
        UserVo userVo = new UserVo(user);
        userVo.setToken(token);

        //传递给前端的数据
        LoginSuccessInfo info = new LoginSuccessInfo(user);
        info.setToken(token);

        Map<String, Integer> userResources = null;
        Set<Integer> userResourcesIdSet = null;

        //为非admin的用户设置权限
        if (!user.isAdmin()) {
            //获取用户的角色
            SysRole role = roleService.getRoleById(roleId);

            if (role != null) {
                //设置角色名称
                String roleName = role.getName();
                userVo.setRoleName(roleName);
                info.setRoleName(roleName);

                //获取用户的权限列表
                List<SysResource> resources = resourceService.getResourceByRole(role);

                userResources = new HashMap<>(resources.size());
                userResourcesIdSet = new HashSet<>(resources.size());

                for (SysResource r : resources) {
                    if (r.isAdmin()) continue;
                    Integer resourceId = r.getId();
                    userResources.put(r.getPath(), r.getId());
                    if (r.getType().equals(ResourceTypeEnum.API.getCode())) {
                        userResourcesIdSet.add(resourceId);
                    }
                }
            }
        }

        //获取用户部门
        Integer deptId = user.getDept();
        if (deptId != null) {
            DepartmentVo dept = departmentService.getById(deptId);
            if (dept != null) {
                //设置用户的部门名称
                String deptName = dept.getFullname();
                userVo.setDeptName(deptName);
                info.setDeptName(deptName);
            }
        }

        //用户的数据范围
        userVo.setDepartmentIds(departmentService.getUserDataScope(user));

        //用户的权限ID集合
        userVo.setResourceIds(userResourcesIdSet);

        //返回给前端的用户权限表
        info.setResources(userResources);

        //用户信息插入redis
        SessionUtil.save(userVo);

        //记录登陆信息
        recService.insertLoginHistory(
                RecLoginHistory
                        .builder()
                        .uid(user.getId())
                        .uname(user.getNickName())
                        .ip(ip)
                        .login(true)
                        .time(now)
                        .build()
        );

        return R.success(info);
    }

    public R logout(UserVo user, String ip) {
        if (user != null) {
            recService.insertLoginHistory(
                    RecLoginHistory
                            .builder()
                            .uid(user.getId())
                            .uname(user.getNickName())
                            .ip(ip)
                            .login(false)
                            .time(System.currentTimeMillis())
                            .build()
            );
            SessionUtil.remove(user.getToken());
        }

        return R.success("登出成功");
    }

    public R register(RegisterParam param) {
        String name = param.getUsername();
        String nick = param.getNick();

        if (isLoginNameExist(name, null)) {
            return R.fail("该登录名已存在");
        }
        if (isNickNameExist(nick, null)) {
            return R.fail("该昵称已存在");
        }

        SysUser user = new SysUser();
        user.setLoginName(name);
        user.setNickName(param.getNick());
        user.setPwd(param.getPassword());
        user.setRole(1);
        user.setDept(1);
        user.setEnable(true);
        user.setCtime(System.currentTimeMillis());

        userMapper.insert(user);

        return R.success("注册成功");
    }

    @UserAction("'修改密码'")
    public R updatePwd(PasswordUpdateParam param) {
        int rows = userMapper.update(
                null,
                Wrappers.lambdaUpdate(SysUser.class)
                        .set(SysUser::getPwd, param.getNewPwd())
                        .eq(SysUser::getId, param.getId())
                        .eq(SysUser::getPwd, param.getOldPwd())
        );
        return rows > 0 ? R.success("修改成功") : R.fail("修改失败，请检查原密码是否正确");
    }

    @UserAction("'修改头像'")
    public R updateAvatar(UserVo user, String avatar) {
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
                fileService.delete(oldAvatar);
            }
            user.setAvatar(avatar);
            SessionUtil.save(user);
            return R.success("上传头像成功", avatar);
        }

        //否则删除此次上传至云的头像
        fileService.delete(avatar);

        return R.fail("上传头像失败");
    }

    //登录名重复时返回true
    public boolean isLoginNameExist(String name, Integer id) {
        Integer num = userMapper.selectCount(
                Wrappers.lambdaQuery(SysUser.class)
                        .eq(SysUser::getLoginName, name)
                        .ne(id != null, SysUser::getId, id)
        );
        return num != null && num > 0;
    }

    //用户昵称重复时返回true
    public boolean isNickNameExist(String name, Integer id) {
        Integer num = userMapper.selectCount(
                Wrappers.lambdaQuery(SysUser.class)
                        .eq(SysUser::getNickName, name)
                        .ne(id != null, SysUser::getId, id)
        );
        return num != null && num > 0;
    }
}

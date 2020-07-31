package cn.toesbieya.jxc.service;

import cn.toesbieya.jxc.annoation.TimeCost;
import cn.toesbieya.jxc.annoation.UserAction;
import cn.toesbieya.jxc.enumeration.GeneralStatusEnum;
import cn.toesbieya.jxc.enumeration.RecLoginHistoryEnum;
import cn.toesbieya.jxc.mapper.SysRoleMapper;
import cn.toesbieya.jxc.mapper.SysUserMapper;
import cn.toesbieya.jxc.model.entity.SysResource;
import cn.toesbieya.jxc.model.entity.SysRole;
import cn.toesbieya.jxc.model.entity.SysUser;
import cn.toesbieya.jxc.model.vo.*;
import cn.toesbieya.jxc.utils.QiniuUtil;
import cn.toesbieya.jxc.model.vo.Result;
import cn.toesbieya.jxc.utils.SessionUtil;
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
    private SysRoleMapper roleMapper;

    @TimeCost
    public Result login(LoginParam param, String ip) {
        SysUser user = userMapper.getByNameAndPwd(param.getUsername(), param.getPassword());

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
            SysRole role = roleMapper.selectById(roleId);

            if (role != null) {
                //设置角色名称
                String roleName = role.getName();
                userVo.setRoleName(roleName);
                info.setRoleName(roleName);

                //获取用户的权限列表
                List<SysResource> resources = resourceService.getByRole(roleId);

                userResourcesUrlMap = new HashMap<>(128);
                userResourcesIdSet = new HashSet<>(128);

                for (SysResource resource : resources) {
                    Integer resourceId = resource.getId();
                    userResourcesUrlMap.put(resource.getUrl(), resourceId);
                    userResourcesIdSet.add(resourceId);
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

        userMapper.insert(user);

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

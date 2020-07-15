package com.toesbieya.my.service;

import com.github.pagehelper.PageHelper;
import com.toesbieya.my.annoation.UserAction;
import com.toesbieya.my.mapper.SysUserMapper;
import com.toesbieya.my.model.entity.SysUser;
import com.toesbieya.my.model.vo.UserVo;
import com.toesbieya.my.model.vo.result.PageResult;
import com.toesbieya.my.model.vo.search.UserSearch;
import com.toesbieya.my.utils.Result;
import com.toesbieya.my.utils.Util;
import com.toesbieya.my.utils.WebSocketUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysUserService {
    private static final String DEFAULT_PWD = DigestUtils.md5DigestAsHex("123456".getBytes());
    @Resource
    private SysUserMapper userMapper;

    public PageResult<UserVo> search(UserSearch vo) {
        PageHelper.startPage(vo.getPage(), vo.getPageSize());

        List<UserVo> users = userMapper.search(vo);

        //当前在线的用户id
        Set<Integer> onlineUserIds = WebSocketUtil.getOnlineUserIds();

        //设置在线情况
        for (UserVo user : users) {
            user.setOnline(Util.some(onlineUserIds, i -> user.getId().equals(i)));
        }

        return new PageResult<>(users);
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

    @UserAction("'修改用户：'+#user.name")
    public Result update(SysUser user) {
        if (userMapper.isNameExist(user.getName(), user.getId())) {
            return Result.fail("该用户名称已存在");
        }

        userMapper.update(user);

        return Result.success("修改成功");
    }

    @UserAction("'删除用户：'+#user.name")
    @Transactional(rollbackFor = Exception.class)
    public Result del(SysUser user) {
        int rows = userMapper.del(user.getId());
        WebSocketUtil.sendLogoutEvent(Collections.singletonList(user.getId()), "该用户已删除");
        return rows > 0 ? Result.success("删除成功") : Result.fail("删除失败，请刷新后重试");
    }

    @UserAction
    public Result kick(List<SysUser> users) {
        WebSocketUtil.sendLogoutEvent(
                users
                        .stream()
                        .map(SysUser::getId)
                        .collect(Collectors.toList()),
                "你已被强制下线，请重新登陆"
        );

        return Result.success("踢出成功");
    }

    @UserAction("'重置用户密码：'+#user.name")
    public Result resetPwd(SysUser user) {
        int rows = userMapper.resetPwd(user.getId(), DEFAULT_PWD);
        return rows > 0 ? Result.success() : Result.fail("重置失败，未匹配到用户");
    }
}

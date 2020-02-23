package com.toesbieya.my.mapper;

import com.toesbieya.my.model.entity.SysUser;
import com.toesbieya.my.model.vo.search.UserSearch;
import com.toesbieya.my.model.vo.update.UserUpdatePwd;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserMapper {
    SysUser getById(@Param("id") Integer id);

    SysUser getByNameAndPwd(@Param("name") String name, @Param("pwd") String pwd);

    List<SysUser> search(UserSearch vo);

    boolean isNameExist(@Param("name") String name, @Param("id") Integer id);

    int add(SysUser user);

    int update(SysUser user);

    int del(@Param("id") Integer id);

    int resetPwd(@Param("id") Integer id, @Param("pwd") String pwd);

    int updatePwd(UserUpdatePwd vo);

    int updateAvatar(@Param("id") Integer id, @Param("avatar") String avatar);
}

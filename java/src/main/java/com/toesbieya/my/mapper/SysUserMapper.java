package com.toesbieya.my.mapper;

import com.toesbieya.my.model.entity.SysUser;
import com.toesbieya.my.model.vo.PasswordUpdateParam;
import com.toesbieya.my.model.vo.UserVo;
import com.toesbieya.my.model.vo.search.UserSearch;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserMapper {
    SysUser getByNameAndPwd(@Param("name") String name, @Param("pwd") String pwd);

    List<UserVo> search(UserSearch vo);

    boolean isNameExist(@Param("name") String name, @Param("id") Integer id);

    int add(SysUser user);

    int update(SysUser user);

    int del(@Param("id") Integer id);

    int resetPwd(@Param("id") Integer id, @Param("pwd") String pwd);

    int updatePwd(PasswordUpdateParam vo);

    int updateAvatar(@Param("id") Integer id, @Param("avatar") String avatar);
}

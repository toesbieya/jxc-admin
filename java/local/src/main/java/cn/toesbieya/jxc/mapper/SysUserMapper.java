package cn.toesbieya.jxc.mapper;

import cn.toesbieya.jxc.model.entity.SysUser;
import cn.toesbieya.jxc.model.vo.PasswordUpdateParam;
import cn.toesbieya.jxc.model.vo.UserVo;
import cn.toesbieya.jxc.model.vo.search.UserSearch;
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

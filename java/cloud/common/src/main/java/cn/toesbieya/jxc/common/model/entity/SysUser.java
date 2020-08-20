package cn.toesbieya.jxc.common.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class SysUser implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String loginName;
    private String nickName;
    private String pwd;
    private Integer role;
    private String avatar;
    private Long ctime;
    private boolean admin = false;
    private boolean enable = false;
    private Integer dept;
}

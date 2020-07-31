package cn.toesbieya.jxc.common.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysUser implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private String pwd;
    private Integer role;
    private String avatar;
    private Long ctime;
    private boolean admin = false;
    private Integer status;
    private Integer dept;
}

package cn.toesbieya.jxc.common.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class SysDepartment implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer pid;
    private String name;
    private Integer status;
}

package cn.toesbieya.jxc.model.entity;

import lombok.Data;

@Data
public class SysCategory {
    private Integer id;
    private Integer pid;
    private String name;
    private boolean leaf = false;
    private Long ctime;
}

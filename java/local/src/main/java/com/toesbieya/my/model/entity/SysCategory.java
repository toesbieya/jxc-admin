package com.toesbieya.my.model.entity;

import lombok.Data;

@Data
public class SysCategory {
    private Integer id;
    private Integer pid;
    private String name;
    private Integer type;
    private Long ctime;
}

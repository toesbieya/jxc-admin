package com.toesbieya.my.model.entity;

import lombok.Data;

@Data
public class SysSupplier {
    private Integer id;
    private String name;
    private String address;
    private String linkman;
    private String linkphone;
    private String region;
    private String region_name;
    private Integer status;
    private Long ctime;
    private String remark;
}

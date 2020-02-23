package com.toesbieya.my.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysResource {
    private Integer id;
    private Integer pid;
    private String name;
    private String fullName;
    private String url;
    private Integer admin;
    private Integer total_rate;
    private Integer ip_rate;
}

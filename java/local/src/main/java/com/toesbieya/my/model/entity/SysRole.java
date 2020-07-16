package com.toesbieya.my.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SysRole {
    private Integer id;
    private String name;
    private String resource_id;
    private Integer cid;
    private String cname;
    private Long ctime;
    private Integer status;
}

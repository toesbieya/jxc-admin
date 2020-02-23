package com.toesbieya.my.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysUser implements Serializable {
    private static final long serialVersionUID = 9527L;
    private Integer id;
    private String name;
    private String pwd;
    private Integer role;
    private String role_name;
    private String avatar;
    private Long ctime;
    private Integer admin;
    private Integer status;
    private Boolean online;
    private HashSet<Integer> resource_ids;

    public static String getUpdateInfo(SysUser origin, SysUser update) {
        StringBuilder stringBuilder = new StringBuilder();
        if (!origin.getName().equals(update.getName())) {
            stringBuilder.append(String.format("，名称改为【%s】", update.getName()));
        }
        if (!origin.getRole().equals(update.getRole())) {
            stringBuilder.append(String.format("，角色改为【%s】", update.getRole_name()));
        }
        if (!origin.getStatus().equals(update.getStatus())) {
            stringBuilder.append(String.format("，状态改为【%s】", update.getStatus() == 1 ? "正常" : "禁用"));
        }
        return stringBuilder.toString();
    }
}

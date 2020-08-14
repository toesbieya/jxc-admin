package cn.toesbieya.jxc.model.entity;

import cn.toesbieya.jxc.enumeration.DataScopeEnum;
import lombok.Data;

@Data
public class SysRole {
    private Integer id;
    private String name;
    private Integer cid;
    private String cname;
    private Long ctime;
    private boolean enable = false;
    private int scope = DataScopeEnum.ALL.getCode();
    private String departmentId;
    private String resourceId;
}

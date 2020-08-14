package cn.toesbieya.jxc.common.model.entity;

import cn.toesbieya.jxc.common.enumeration.DataScopeEnum;
import lombok.Data;

import java.io.Serializable;

@Data
public class SysRole implements Serializable {
    private static final long serialVersionUID = 1L;
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

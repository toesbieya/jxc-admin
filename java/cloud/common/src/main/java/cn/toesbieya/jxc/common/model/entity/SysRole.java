package cn.toesbieya.jxc.common.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class SysRole implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private String resource_id;
    private Integer cid;
    private String cname;
    private Long ctime;
    private Integer status;
}

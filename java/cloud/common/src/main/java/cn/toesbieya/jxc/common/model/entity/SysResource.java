package cn.toesbieya.jxc.common.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class SysResource implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer pid;
    private Integer type;
    private String name;
    private String path;
    private String component;
    private String meta;
    private boolean admin = false;
    private boolean enable = true;
}

package cn.toesbieya.jxc.common.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class SysRegion implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String pid;
    private String name;
    private String fullname;
    private int level;
}

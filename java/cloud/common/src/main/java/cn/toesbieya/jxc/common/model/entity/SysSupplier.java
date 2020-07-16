package cn.toesbieya.jxc.common.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class SysSupplier implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private String address;
    private String linkman;
    private String linkphone;
    private String region;
    private Integer status;
    private Long ctime;
    private String remark;
}

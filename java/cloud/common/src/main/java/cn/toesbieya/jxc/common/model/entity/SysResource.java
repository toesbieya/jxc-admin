package cn.toesbieya.jxc.common.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class SysResource implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer pid;
    private String name;
    private String url;
    private boolean admin = false;
    private Long totalRate;
    private Long ipRate;
}

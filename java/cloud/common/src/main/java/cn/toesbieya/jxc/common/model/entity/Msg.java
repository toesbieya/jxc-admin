package cn.toesbieya.jxc.common.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Msg implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String title;
    private String content;
    private Integer type;
    private Integer cid;
    private String cname;
    private Long ctime;
    private Integer pid;
    private String pname;
    private Long ptime;
    private Integer wid;
    private String wname;
    private Long wtime;
    private Integer status;
    private boolean broadcast = false;
    private String recipient;
}

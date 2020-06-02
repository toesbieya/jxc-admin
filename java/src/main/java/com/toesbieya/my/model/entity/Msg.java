package com.toesbieya.my.model.entity;

import lombok.Data;

@Data
public class Msg {
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
    private Integer all;
    private String recipient;
}

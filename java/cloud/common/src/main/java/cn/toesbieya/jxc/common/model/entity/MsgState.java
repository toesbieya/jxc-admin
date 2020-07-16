package cn.toesbieya.jxc.common.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class MsgState implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer mid;
    private Integer uid;
    private Long time;
}

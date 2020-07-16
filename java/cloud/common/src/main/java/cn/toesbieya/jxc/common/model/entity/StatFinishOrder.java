package cn.toesbieya.jxc.common.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class StatFinishOrder implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer purchase;
    private Integer sell;
    private Long time;
}

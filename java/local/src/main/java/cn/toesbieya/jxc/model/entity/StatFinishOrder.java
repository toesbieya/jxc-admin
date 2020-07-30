package cn.toesbieya.jxc.model.entity;

import lombok.Data;

@Data
public class StatFinishOrder {
    private Integer id;
    private Integer purchase;
    private Integer sell;
    private Long time;
}

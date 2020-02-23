package com.toesbieya.my.model.entity;

import lombok.Data;

@Data
public class StatProfitTotal {
    private Integer id;
    private Double purchase;
    private Double sell;
    private Double profit;
    private Long time;
}

package cn.toesbieya.jxc.model.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StatProfitTotal {
    private Integer id;
    private BigDecimal purchase;
    private BigDecimal sell;
    private BigDecimal profit;
    private Long time;
}

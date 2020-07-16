package cn.toesbieya.jxc.common.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class StatProfitTotal implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private BigDecimal purchase;
    private BigDecimal sell;
    private BigDecimal profit;
    private Long time;
}

package cn.toesbieya.jxc.statistic.model.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FourBlockStat {
    private long online;
    private BigDecimal purchase;
    private BigDecimal sell;
    private BigDecimal profit;
}

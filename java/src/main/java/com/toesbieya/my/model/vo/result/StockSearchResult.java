package com.toesbieya.my.model.vo.result;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockSearchResult {
    private Integer cid;
    private String cname;
    private BigDecimal total_num;
    private BigDecimal total_price;
}

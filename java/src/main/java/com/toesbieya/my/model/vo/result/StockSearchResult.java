package com.toesbieya.my.model.vo.result;

import lombok.Data;

@Data
public class StockSearchResult {
    private Integer cid;
    private String cname;
    private Double total_num;
    private Double total_price;
}

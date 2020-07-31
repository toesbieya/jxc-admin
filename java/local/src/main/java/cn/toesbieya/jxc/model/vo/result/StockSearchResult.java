package cn.toesbieya.jxc.model.vo.result;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockSearchResult {
    private Integer cid;
    private String cname;
    private BigDecimal totalNum;
    private BigDecimal totalPrice;
}

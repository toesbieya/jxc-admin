package cn.toesbieya.jxc.common.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class StockSearchResult implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer cid;
    private String cname;
    private BigDecimal total_num;
    private BigDecimal total_price;
}

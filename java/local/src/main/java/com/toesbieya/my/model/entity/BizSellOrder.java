package com.toesbieya.my.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BizSellOrder extends BizDocument<BizSellOrderSub> {
    private Integer customer_id;
    private String customer_name;
    private Integer finish;
    private Long ftime;
    private BigDecimal total;
}

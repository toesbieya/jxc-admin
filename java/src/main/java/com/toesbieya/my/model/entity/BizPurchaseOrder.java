package com.toesbieya.my.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BizPurchaseOrder extends BizDocument<BizPurchaseOrderSub> {
    private Integer sid;
    private String sname;
    private Integer finish;
    private Long ftime;
    private Double total;
}

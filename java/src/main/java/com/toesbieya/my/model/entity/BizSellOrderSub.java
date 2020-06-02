package com.toesbieya.my.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BizSellOrderSub extends BizDocumentSub {
    private Double price;
    private Double remain_num;
}

package cn.toesbieya.jxc.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BizPurchaseOrderSub extends BizDocumentSub {
    private BigDecimal price;
    private BigDecimal remainNum;
}

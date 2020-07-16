package cn.toesbieya.jxc.common.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BizPurchaseOrderSub extends BizDocSub {
    private BigDecimal price;
    private BigDecimal remain_num;
}

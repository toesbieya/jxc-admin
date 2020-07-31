package cn.toesbieya.jxc.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BizSellOrder extends BizDocument<BizSellOrderSub> {
    private Integer customerId;
    private String customerName;
    private Integer finish;
    private Long ftime;
    private BigDecimal total;
}

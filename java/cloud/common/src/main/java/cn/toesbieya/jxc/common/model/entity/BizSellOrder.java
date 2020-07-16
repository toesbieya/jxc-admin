package cn.toesbieya.jxc.common.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class BizSellOrder extends BizDoc {
    private Integer customer_id;
    private String customer_name;
    private Integer finish;
    private Long ftime;
    private BigDecimal total;

    public BizSellOrder(BizSellOrder obj) {
        super(obj);
        this.customer_id = obj.getCustomer_id();
        this.customer_name = obj.getCustomer_name();
        this.finish = obj.getFinish();
        this.ftime = obj.getFtime();
        this.total = obj.getTotal();
    }
}

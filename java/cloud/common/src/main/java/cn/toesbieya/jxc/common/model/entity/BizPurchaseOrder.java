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
public class BizPurchaseOrder extends BizDoc {
    private Integer sid;
    private String sname;
    private Integer finish;
    private Long ftime;
    private BigDecimal total;
}

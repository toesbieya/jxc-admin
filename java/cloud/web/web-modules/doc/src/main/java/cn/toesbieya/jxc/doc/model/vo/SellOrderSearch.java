package cn.toesbieya.jxc.doc.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SellOrderSearch extends DocSearch {
    private Integer customerId;
    private String customerName;
    private String finish;
    private Long ftimeStart;
    private Long ftimeEnd;
}

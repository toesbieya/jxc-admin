package cn.toesbieya.jxc.model.vo.search;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SellOrderSearch extends DocumentSearch {
    private Integer customerId;
    private String customerName;
    private String finish;
    private Long ftimeStart;
    private Long ftimeEnd;
}

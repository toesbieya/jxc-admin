package cn.toesbieya.jxc.model.vo.search;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PurchaseOrderSearch extends DocSearch {
    private Integer sid;
    private String sname;
    private String finish;
    private Long ftimeStart;
    private Long ftimeEnd;
}

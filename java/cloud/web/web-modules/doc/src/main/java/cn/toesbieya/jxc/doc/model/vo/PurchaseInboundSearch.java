package cn.toesbieya.jxc.doc.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PurchaseInboundSearch extends DocSearch {
    private String pid;
    private String pidFuzzy;
}

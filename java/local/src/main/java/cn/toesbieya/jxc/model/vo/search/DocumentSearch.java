package cn.toesbieya.jxc.model.vo.search;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DocumentSearch extends BaseSearch {
    private String id;
    private String idFuzzy;
    private Integer cid;
    private String cname;
    private Integer vid;
    private String vname;
    private String status;
    private Long ctimeStart;
    private Long ctimeEnd;
    private Long vtimeStart;
    private Long vtimeEnd;
}

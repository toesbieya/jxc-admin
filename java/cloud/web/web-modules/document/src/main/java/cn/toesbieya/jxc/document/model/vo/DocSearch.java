package cn.toesbieya.jxc.document.model.vo;

import cn.toesbieya.jxc.common.model.vo.BaseSearch;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DocSearch extends BaseSearch {
    private String id;
    private String id_fuzzy;
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

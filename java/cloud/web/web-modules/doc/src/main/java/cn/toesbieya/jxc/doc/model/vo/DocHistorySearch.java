package cn.toesbieya.jxc.doc.model.vo;

import cn.toesbieya.jxc.common.model.vo.BaseSearch;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DocHistorySearch extends BaseSearch {
    private String pid;
    private String type;
    private String uid;
    private String uname;
}

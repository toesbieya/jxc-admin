package cn.toesbieya.jxc.model.vo.search;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DocHistorySearch extends BaseSearch{
    private String pid;
    private String type;
    private String uid;
    private String uname;
}

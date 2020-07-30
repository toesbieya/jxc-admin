package cn.toesbieya.jxc.model.vo.search;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MsgPersonalSearch extends BaseSearch {
    private Integer uid;
    private Long ctime;
    private boolean unread = true;
}

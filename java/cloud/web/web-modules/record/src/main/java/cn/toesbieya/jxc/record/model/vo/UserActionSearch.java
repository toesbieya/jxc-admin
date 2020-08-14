package cn.toesbieya.jxc.record.model.vo;

import cn.toesbieya.jxc.common.model.vo.BaseSearch;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserActionSearch extends BaseSearch {
    private Integer uid;
    private String uname;
    private String ip;
    private String url;
    private Long startTime;
    private Long endTime;
    private Boolean success;
}

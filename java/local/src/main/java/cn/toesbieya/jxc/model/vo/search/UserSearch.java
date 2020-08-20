package cn.toesbieya.jxc.model.vo.search;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserSearch extends BaseSearch {
    private Integer id;
    private String loginName;
    private String nickName;
    private String role;
    private Long startTime;
    private Long endTime;
    private Boolean enable;
}

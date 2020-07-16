package cn.toesbieya.jxc.system.model.vo;

import cn.toesbieya.jxc.common.model.vo.BaseSearch;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserSearch extends BaseSearch {
    private Integer id;
    private String name;
    private String role;
    private Long startTime;
    private Long endTime;
    private Integer status;
}

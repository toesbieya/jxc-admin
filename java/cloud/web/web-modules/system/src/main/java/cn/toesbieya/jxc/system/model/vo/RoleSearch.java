package cn.toesbieya.jxc.system.model.vo;

import cn.toesbieya.jxc.common.model.vo.BaseSearch;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RoleSearch extends BaseSearch {
    private Integer id;
    private String name;
    private Integer cid;
    private String cname;
    private Integer status;
    private Long startTime;
    private Long endTime;
}

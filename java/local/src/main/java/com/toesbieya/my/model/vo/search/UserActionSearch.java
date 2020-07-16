package com.toesbieya.my.model.vo.search;

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
    private String type;
}

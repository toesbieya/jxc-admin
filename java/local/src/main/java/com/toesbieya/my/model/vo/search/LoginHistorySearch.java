package com.toesbieya.my.model.vo.search;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class LoginHistorySearch extends BaseSearch {
    private Integer uid;
    private String uname;
    private Integer type;
    private String ip;
    private String address;
    private Long endTime;
    private Long startTime;
}

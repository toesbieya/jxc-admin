package com.toesbieya.my.model.vo.search;

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

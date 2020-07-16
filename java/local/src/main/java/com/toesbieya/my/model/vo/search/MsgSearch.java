package com.toesbieya.my.model.vo.search;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MsgSearch extends BaseSearch {
    private String title;
    private String type;
    private Integer uid;
    private String status;
}

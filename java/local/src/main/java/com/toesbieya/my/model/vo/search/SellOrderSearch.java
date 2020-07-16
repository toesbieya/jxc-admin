package com.toesbieya.my.model.vo.search;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SellOrderSearch extends DocumentSearch {
    private Integer customer_id;
    private String customer_name;
    private String finish;
    private Long ftimeStart;
    private Long ftimeEnd;
}

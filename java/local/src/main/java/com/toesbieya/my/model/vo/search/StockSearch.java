package com.toesbieya.my.model.vo.search;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class StockSearch extends BaseSearch {
    private Integer id;
    private String ids;
    private Integer cid;
    private String cids;
    private Long startTime;
    private Long endTime;
    private String cgddid;
    private String cgddids;
    private String cgrkid;
    private String cgrkids;
}

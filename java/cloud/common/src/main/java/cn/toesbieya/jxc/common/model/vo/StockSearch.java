package cn.toesbieya.jxc.common.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class StockSearch extends BaseSearch implements Serializable {
    private static final long serialVersionUID = 1L;
    private String ids;
    private String cids;
    private Long startTime;
    private Long endTime;
    private String cgddids;
    private String cgrkids;
}

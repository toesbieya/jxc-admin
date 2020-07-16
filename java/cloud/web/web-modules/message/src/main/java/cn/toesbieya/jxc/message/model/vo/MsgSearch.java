package cn.toesbieya.jxc.message.model.vo;

import cn.toesbieya.jxc.common.model.vo.BaseSearch;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MsgSearch extends BaseSearch {
    private String title;
    private String type;
    private String status;
}

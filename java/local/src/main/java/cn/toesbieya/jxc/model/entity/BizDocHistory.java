package cn.toesbieya.jxc.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BizDocHistory {
    private Integer id;
    private String pid;
    private Integer type;
    private Integer uid;
    private String uname;
    private Integer statusBefore;
    private Integer statusAfter;
    private Long time;
    private String info;
}

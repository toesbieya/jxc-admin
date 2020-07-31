package cn.toesbieya.jxc.common.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BizDocHistory implements Serializable {
    private static final long serialVersionUID = 1L;
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

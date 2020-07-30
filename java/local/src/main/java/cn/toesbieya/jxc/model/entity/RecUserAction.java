package cn.toesbieya.jxc.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecUserAction {
    private Long id;
    private Integer uid;
    private String uname;
    private String url;
    private String ip;
    private Long time;
    private String action;
    private String error;
    private Integer type;
}

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
public class RecUserAction implements Serializable {
    private static final long serialVersionUID = 1L;
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

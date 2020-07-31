package cn.toesbieya.jxc.common.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class BizDoc implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private Integer cid;
    private String cname;
    private Long ctime;
    private Integer vid;
    private String vname;
    private Long vtime;
    private Integer status;
    private String remark;
}

package cn.toesbieya.jxc.common.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class BizDocSub implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String pid;
    private Integer cid;
    private String cname;
    private BigDecimal num;
}

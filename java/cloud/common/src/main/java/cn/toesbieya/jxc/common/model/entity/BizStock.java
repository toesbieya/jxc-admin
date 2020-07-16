package cn.toesbieya.jxc.common.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BizStock implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer cid;
    private String cname;
    private BigDecimal num;
    private BigDecimal price;
    private Long ctime;
    private String cgrkid;
    private String cgddid;
}
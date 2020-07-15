package com.toesbieya.my.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BizStock {
    private Integer id;
    private Integer cid;
    private String cname;
    private BigDecimal num;
    private BigDecimal price;
    private Long ctime;
    private String cgrkid;
    private String cgddid;
}
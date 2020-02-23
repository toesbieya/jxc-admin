package com.toesbieya.my.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BizStock {
    private Integer id;
    private Integer cid;
    private String cname;
    private Double num;
    private Double price;
    private Long ctime;
    private String cgrkid;
    private String cgddid;
}
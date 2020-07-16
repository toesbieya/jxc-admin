package com.toesbieya.my.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BizDocumentHistory {
    private Integer id;
    private String pid;
    private Integer type;
    private Integer uid;
    private String uname;
    private Integer status_before;
    private Integer status_after;
    private Long time;
    private String info;
}

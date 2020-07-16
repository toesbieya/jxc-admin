package com.toesbieya.my.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecLoginHistory {
    private Long id;
    private Integer uid;
    private String uname;
    private String ip;
    private String address;
    private Integer type;
    private Long time;
}

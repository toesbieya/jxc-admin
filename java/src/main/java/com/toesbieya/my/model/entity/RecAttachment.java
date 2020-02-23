package com.toesbieya.my.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecAttachment {
    private Integer id;
    private String pid;
    private String name;
    private Long time;
    private Integer order;
    private String url;
    private Long size;
}

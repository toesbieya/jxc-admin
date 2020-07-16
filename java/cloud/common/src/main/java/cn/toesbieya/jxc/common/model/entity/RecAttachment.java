package cn.toesbieya.jxc.common.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecAttachment implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String pid;
    private String name;
    private Long time;
    private Integer sort;
    private String url;
    private Long size;
}

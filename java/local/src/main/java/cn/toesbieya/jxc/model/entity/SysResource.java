package cn.toesbieya.jxc.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysResource {
    private Integer id;
    private Integer pid;
    private String name;
    private String url;
    private boolean admin = false;
    private Long totalRate;
    private Long ipRate;
}

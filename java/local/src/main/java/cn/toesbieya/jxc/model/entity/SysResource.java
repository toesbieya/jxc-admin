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
    private Integer type;
    private String name;
    private String path;
    private String component;
    private String meta;
    private boolean admin = false;
    private boolean enable = true;
}

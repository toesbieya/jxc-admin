package cn.toesbieya.jxc.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SysCustomer {
    private Integer id;
    private String name;
    private String address;
    private String linkman;
    private String linkphone;
    private String region;
    private String regionName;
    private Integer status;
    private Long ctime;
    private String remark;
}
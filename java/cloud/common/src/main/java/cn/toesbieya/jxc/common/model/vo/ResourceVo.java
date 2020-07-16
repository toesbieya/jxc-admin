package cn.toesbieya.jxc.common.model.vo;

import cn.toesbieya.jxc.common.model.entity.SysResource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ResourceVo extends SysResource {
    private String fullName;

    public ResourceVo(SysResource parent) {
        this.setId(parent.getId());
        this.setPid(parent.getPid());
        this.setName(parent.getName());
        this.setUrl(parent.getUrl());
        this.setAdmin(parent.getAdmin());
    }
}

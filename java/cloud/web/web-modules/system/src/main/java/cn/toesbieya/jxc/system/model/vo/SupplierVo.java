package cn.toesbieya.jxc.system.model.vo;

import cn.toesbieya.jxc.common.model.entity.SysSupplier;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SupplierVo extends SysSupplier {
    private String region_name;

    public SupplierVo(SysSupplier parent) {
        this.setId(parent.getId());
        this.setName(parent.getName());
        this.setAddress(parent.getAddress());
        this.setLinkman(parent.getLinkman());
        this.setLinkphone(parent.getLinkphone());
        this.setRegion(parent.getRegion());
        this.setStatus(parent.getStatus());
        this.setCtime(parent.getCtime());
        this.setRemark(parent.getRemark());
    }
}

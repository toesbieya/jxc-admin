package cn.toesbieya.jxc.model.vo;

import cn.toesbieya.jxc.model.entity.SysDepartment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DepartmentVo extends SysDepartment {
    String fullname;

    public DepartmentVo(SysDepartment parent) {
        this.setId(parent.getId());
        this.setPid(parent.getPid());
        this.setName(parent.getName());
        this.setStatus(parent.getStatus());
    }
}

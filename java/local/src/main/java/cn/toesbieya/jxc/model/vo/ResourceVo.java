package cn.toesbieya.jxc.model.vo;

import cn.toesbieya.jxc.model.entity.SysResource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ResourceVo extends SysResource {
    private String fullname;

    public ResourceVo(SysResource parent) {
        BeanUtils.copyProperties(parent, this);
    }
}

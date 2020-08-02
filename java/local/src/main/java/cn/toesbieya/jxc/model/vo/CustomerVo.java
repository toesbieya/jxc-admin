package cn.toesbieya.jxc.model.vo;

import cn.toesbieya.jxc.model.entity.SysCustomer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CustomerVo extends SysCustomer {
    private String regionName;

    public CustomerVo(SysCustomer parent) {
        BeanUtils.copyProperties(parent, this);
    }
}

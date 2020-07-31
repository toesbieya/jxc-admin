package cn.toesbieya.jxc.model.vo;

import cn.toesbieya.jxc.model.entity.SysUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.util.Set;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserVo extends SysUser {
    private String token;
    private String roleName;
    private String deptName;
    private boolean online = false;
    private Set<Integer> departmentIds;
    private Set<Integer> resourceIds;

    public UserVo(SysUser parent) {
        BeanUtils.copyProperties(parent,this);
    }
}

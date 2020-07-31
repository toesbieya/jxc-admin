package cn.toesbieya.jxc.common.model.vo;

import cn.toesbieya.jxc.common.model.entity.SysUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
        this.setId(parent.getId());
        this.setName(parent.getName());
        this.setPwd(parent.getPwd());
        this.setRole(parent.getRole());
        this.setAvatar(parent.getAvatar());
        this.setCtime(parent.getCtime());
        this.setAdmin(parent.isAdmin());
        this.setStatus(parent.getStatus());
    }
}

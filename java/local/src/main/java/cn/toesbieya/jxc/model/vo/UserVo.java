package cn.toesbieya.jxc.model.vo;

import cn.toesbieya.jxc.model.entity.SysUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserVo extends SysUser {
    private String token;
    private String role_name;
    private boolean online = false;
    private Set<Integer> resource_ids;
    private Map<String, Integer> resource_map;

    public UserVo(SysUser parent) {
        this.setId(parent.getId());
        this.setName(parent.getName());
        this.setPwd(parent.getPwd());
        this.setRole(parent.getRole());
        this.setAvatar(parent.getAvatar());
        this.setCtime(parent.getCtime());
        this.setAdmin(parent.getAdmin());
        this.setStatus(parent.getStatus());
    }
}

package cn.toesbieya.jxc.account.vo;

import cn.toesbieya.jxc.common.model.entity.SysUser;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class LoginSuccessInfo {
    private Integer id;
    private String name;
    private String role_name;
    private String avatar;
    private Integer admin;
    private String token;
    private Map<String, Integer> resources;

    public LoginSuccessInfo(SysUser user) {
        this.id = user.getId();
        this.name = user.getName();
        this.avatar = user.getAvatar();
        this.admin = user.getAdmin();
    }
}

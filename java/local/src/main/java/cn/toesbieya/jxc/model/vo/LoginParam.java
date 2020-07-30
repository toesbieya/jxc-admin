package cn.toesbieya.jxc.model.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class LoginParam {
    @NotNull(message = "用户名不能为空")
    @Length(max = 20, message = "用户名太长了")
    private String username;

    @NotNull(message = "密码不能为空")
    @Length(min = 32, max = 32, message = "密码异常")
    private String password;
}

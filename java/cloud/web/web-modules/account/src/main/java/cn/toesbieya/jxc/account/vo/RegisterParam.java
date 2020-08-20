package cn.toesbieya.jxc.account.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RegisterParam extends LoginParam {
    @NotNull(message = "昵称不能为空")
    @Length(max = 100, message = "昵称太长了")
    private String nick;
}

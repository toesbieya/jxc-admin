package cn.toesbieya.jxc.model.vo;

import lombok.Data;

@Data
public class PasswordUpdateParam {
    private Integer id;
    private String new_pwd;
    private String old_pwd;
}

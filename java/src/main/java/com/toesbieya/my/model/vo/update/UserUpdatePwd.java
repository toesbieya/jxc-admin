package com.toesbieya.my.model.vo.update;

import lombok.Data;

@Data
public class UserUpdatePwd {
    private Integer id;
    private String old_pwd;
    private String new_pwd;
}

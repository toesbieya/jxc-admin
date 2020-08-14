package cn.toesbieya.jxc.common.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecLoginHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Integer uid;
    private String uname;
    private String ip;
    private String address;
    private boolean login = false;
    private Long time;
}

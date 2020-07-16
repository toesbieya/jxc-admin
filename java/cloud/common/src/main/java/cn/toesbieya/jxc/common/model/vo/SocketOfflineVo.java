package cn.toesbieya.jxc.common.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocketOfflineVo {
    //用户的sessionKey
    private String key;

    //断开时间
    private Long time;
}

package com.toesbieya.my.model.vo;

import com.toesbieya.my.constant.SocketConstant;
import lombok.Data;

import java.util.List;

@Data
public class SocketEventVo {
    //事件类型
    private int type = SocketConstant.REDIS_EVENT_SPECIFIC;

    //事件名称
    private String event;

    //发送对象的id集合
    private List<Integer> to;

    //数据包
    private Object data;
}

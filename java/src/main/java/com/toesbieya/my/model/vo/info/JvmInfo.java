package com.toesbieya.my.model.vo.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JvmInfo {
    //jvm最大可申请内存
    private long max;
    //jvm当前总内存
    private long total;
    //已使用内存
    private long used;
    //空闲内存
    private long remain;
    //使用率
    private double utilizationRate;

    public JvmInfo(long max, long total, long remain) {
        this.max = max;
        this.total = total;
        this.remain = remain;
        this.used = total - remain;
        this.utilizationRate = this.used * 1.0 / total * 100;
    }
}

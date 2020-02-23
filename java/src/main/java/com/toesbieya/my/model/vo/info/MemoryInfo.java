package com.toesbieya.my.model.vo.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemoryInfo {
    //总内存
    private long total;
    //已使用内存
    private long used;
    //剩余内存
    private long remain;
    //内存使用率
    private double utilizationRate;

    public MemoryInfo(long total, long remain) {
        this.total = total;
        this.remain = remain;
        this.used = total - remain;
        this.utilizationRate = this.used * 1.0 / total * 100;
    }
}

package com.toesbieya.my.model.vo.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import oshi.software.os.OSFileStore;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiskInfo {
    private String name;
    private long total;
    private long used;
    private long remain;
    private double utilizationRate;

    public DiskInfo(OSFileStore fs) {
        this.name = fs.getMount();
        this.total = fs.getTotalSpace();
        this.remain = fs.getUsableSpace();
        this.used = this.total - this.remain;
        this.utilizationRate = this.used * 1.0 / this.total * 100;
    }
}

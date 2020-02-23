package com.toesbieya.my.model.vo.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import oshi.software.os.OperatingSystem;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperatingInfo {
    private long bootedTime;
    private long upTime;

    public OperatingInfo(OperatingSystem os) {
        this.bootedTime = os.getSystemBootTime();
        this.upTime = os.getSystemUptime();
    }
}

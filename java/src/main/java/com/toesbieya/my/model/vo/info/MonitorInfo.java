package com.toesbieya.my.model.vo.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonitorInfo {
    private OperatingInfo operatingInfo;
    private CpuInfo cpuInfo;
    private MemoryInfo memoryInfo;
    private JvmInfo jvmInfo;
    private List<DiskInfo> diskInfos;
    private Long timestamp;
    private Integer expire;
}

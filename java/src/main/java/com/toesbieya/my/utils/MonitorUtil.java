package com.toesbieya.my.utils;

import com.alibaba.fastjson.JSONObject;
import com.toesbieya.my.model.vo.info.*;
import com.toesbieya.my.model.vo.info.*;
import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MonitorUtil {
    private final static String REDIS_MONITOR_INFO_KEY = "SYSTEM_MONITOR_INFO_CACHE";
    private final static int EXPIRE = 30;
    private final static SystemInfo SYSTEM_INFO = new SystemInfo();

    public static MonitorInfo getMonitorInfo() {
        long timestamp = System.currentTimeMillis();
        JSONObject cache = (JSONObject) RedisUtil.get(REDIS_MONITOR_INFO_KEY);
        if (cache == null) {
            MonitorInfo monitorInfo = new MonitorInfo(getOperatingInfo(), getCpuInfo(), getMemoryInfo(), getJvmInfo(), getDiskInfo(), timestamp, EXPIRE);
            RedisUtil.set(REDIS_MONITOR_INFO_KEY, monitorInfo, EXPIRE);
            return monitorInfo;
        }
        else {
            return JSONObject.toJavaObject(cache, MonitorInfo.class);
        }
    }

    private static OperatingInfo getOperatingInfo() {
        OperatingSystem os = SYSTEM_INFO.getOperatingSystem();
        return new OperatingInfo(os);
    }

    private static CpuInfo getCpuInfo() {
        CentralProcessor processor = SYSTEM_INFO.getHardware().getProcessor();
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        Util.sleep(1000);
        long[] ticks = processor.getSystemCpuLoadTicks();
        return new CpuInfo(processor, prevTicks, ticks);
    }

    private static MemoryInfo getMemoryInfo() {
        GlobalMemory memory = SYSTEM_INFO.getHardware().getMemory();
        return new MemoryInfo(memory.getTotal(), memory.getAvailable());
    }

    private static JvmInfo getJvmInfo() {
        Runtime runtime = Runtime.getRuntime();
        return new JvmInfo(runtime.maxMemory(), runtime.totalMemory(), runtime.freeMemory());
    }

    private static List<DiskInfo> getDiskInfo() {
        FileSystem fileSystem = SYSTEM_INFO.getOperatingSystem().getFileSystem();
        OSFileStore[] fsArray = fileSystem.getFileStores();
        List<DiskInfo> list = new ArrayList<>();
        for (OSFileStore fs : fsArray) {
            list.add(new DiskInfo(fs));
        }
        return list;
    }
}

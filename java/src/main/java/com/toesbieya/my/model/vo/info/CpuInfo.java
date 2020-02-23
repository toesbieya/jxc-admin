package com.toesbieya.my.model.vo.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import oshi.hardware.CentralProcessor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CpuInfo {
    //cpu名称
    private String name;
    //核心数
    private int core;
    //内核态CPU时间
    private double system;
    //用户态CPU时间
    private double user;
    //低优先级用户态CPU时间
    private double nice;
    //空闲时间，不包括io等待时间
    private double idle;
    //等待I/O的CPU时间
    private double ioWait;
    //处理硬中断的CPU时间
    private double irq;
    //处理软中断的CPU时间
    private double softIrq;
    //运行在虚拟机中的时候，被其他虚拟机占用的CPU时间
    private double steal;

    public CpuInfo(CentralProcessor processor, long[] prevTicks, long[] ticks) {
        this.name = processor.getProcessorIdentifier().getName().trim();
        this.core = processor.getLogicalProcessorCount();
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softIrq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long system = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long ioWait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long total = user + nice + system + idle + ioWait + irq + softIrq + steal;
        this.system = system * 1.0 / total * 100;
        this.nice = nice * 1.0 / total * 100;
        this.user = user * 1.0 / total * 100;
        this.idle = idle * 1.0 / total * 100;
        this.ioWait = ioWait * 1.0 / total * 100;
        this.irq = irq * 1.0 / total * 100;
        this.softIrq = softIrq * 1.0 / total * 100;
        this.steal = steal * 1.0 / total * 100;
    }
}

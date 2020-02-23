<template>
    <div>
        <div class="tip-row">
            <span>启动时间：{{operatingInfo.bootedTime}}</span>
            <span style="margin-left: 20px">运行时间：{{operatingInfo.upTime|timePass}}</span>
        </div>
        <cpu-info :data="cpuInfo"/>
        <el-row :gutter="40" style="margin-top: 32px">
            <el-col :span="12">
                <memory-info :data="memoryInfo"/>
            </el-col>
            <el-col :span="12">
                <jvm-info :data="jvmInfo"/>
            </el-col>
        </el-row>
    </div>
</template>

<script>
    import {getMonitorInfo} from "@/api/system/monitor"
    import CpuInfo from './components/Cpu'
    import MemoryInfo from './components/Memory'
    import JvmInfo from './components/Jvm'
    import {sub, timeFormat} from "@/utils"

    export default {
        name: "systemMonitor",
        components: {CpuInfo, MemoryInfo, JvmInfo},
        data() {
            return {
                loading: false,
                info: {
                    diskInfo: [{
                        "name": "",
                        "remain": 0,
                        "total": 0,
                        "used": 0,
                        "utilizationRate": 0
                    }]
                },
                expire: 60,
                time: '',
                refreshTimer: null,
                operatingInfo: {
                    bootedTime: 'NaN',
                    upTime: 'NaN'
                },
                cpuInfo: {
                    name: '',
                    core: 'NaN',
                    utilizationRate: 0.00,
                    lastUtilizationRate: 0.00,
                    maxUtilizationRate: 0.00,
                    lastMaxUtilizationRate: 0.00,
                    xData: [],
                    vData: []
                },
                memoryInfo: {
                    total: 0,
                    used: 0,
                    utilizationRate: 0.00,
                    lastUtilizationRate: 0.00,
                    maxUtilizationRate: 0.00,
                    lastMaxUtilizationRate: 0.00,
                    xData: [],
                    vData: []
                },
                jvmInfo: {
                    total: 0,
                    used: 0,
                    remain: 0,
                    utilizationRate: 0.00,
                    lastUtilizationRate: 0.00,
                    maxUtilizationRate: 0.00,
                    lastMaxUtilizationRate: 0.00,
                    xData: [],
                    vData: []
                }
            }
        },
        methods: {
            getData() {
                if (this.loading) return
                this.loading = true
                getMonitorInfo()
                    .then(data => {
                        this.expire = data.expire
                        this.time = timeFormat('HH:mm:ss', new Date(data.timestamp))
                        this.handleOperatingInfo(data.operatingInfo)
                        this.handleCpuInfo(data.cpuInfo)
                        this.handleMemoryInfo(data.memoryInfo)
                        this.handleJvmInfo(data.jvmInfo)
                    })
                    .finally(() => this.loading = false)
            },
            handleOperatingInfo(info) {
                this.operatingInfo.bootedTime = timeFormat(null, new Date(info.bootedTime * 1000))
                this.operatingInfo.upTime = info.upTime
            },
            handleCpuInfo(info) {
                let used = (Number)(sub(100, info.idle).toFixed(2))
                this.cpuInfo.name = info.name
                this.cpuInfo.core = info.core
                this.cpuInfo.lastUtilizationRate = this.cpuInfo.utilizationRate
                this.cpuInfo.utilizationRate = used
                if (used > this.cpuInfo.maxUtilizationRate) {
                    this.cpuInfo.lastMaxUtilizationRate = this.cpuInfo.maxUtilizationRate
                    this.cpuInfo.maxUtilizationRate = used
                }
                this.cpuInfo.xData.push(this.time)
                this.cpuInfo.vData.push(used)
            },
            handleMemoryInfo(info) {
                info.utilizationRate = (Number)(info.utilizationRate.toFixed(2))
                this.memoryInfo.total = info.total
                this.memoryInfo.used = info.used
                this.memoryInfo.remain = info.remain
                this.memoryInfo.lastUtilizationRate = this.memoryInfo.utilizationRate
                this.memoryInfo.utilizationRate = info.utilizationRate
                if (info.utilizationRate > this.memoryInfo.maxUtilizationRate) {
                    this.memoryInfo.lastMaxUtilizationRate = this.memoryInfo.maxUtilizationRate
                    this.memoryInfo.maxUtilizationRate = info.utilizationRate
                }
                this.memoryInfo.xData.push(this.time)
                this.memoryInfo.vData.push(info.utilizationRate)
            },
            handleJvmInfo(info) {
                info.utilizationRate = (Number)(info.utilizationRate.toFixed(2))
                this.jvmInfo.total = info.total
                this.jvmInfo.used = info.used
                this.jvmInfo.remain = info.remain
                this.jvmInfo.lastUtilizationRate = this.jvmInfo.utilizationRate
                this.jvmInfo.utilizationRate = info.utilizationRate
                if (info.utilizationRate > this.jvmInfo.maxUtilizationRate) {
                    this.jvmInfo.lastMaxUtilizationRate = this.jvmInfo.maxUtilizationRate
                    this.jvmInfo.maxUtilizationRate = info.utilizationRate
                }
                this.jvmInfo.xData.push(this.time)
                this.jvmInfo.vData.push(info.utilizationRate)
            },
        },
        mounted() {
            this.getData()
            this.refreshTimer = setInterval(() => this.getData(), 40000)
        }
    }
</script>

<style lang="scss">
    .chart-card {
        width: 100%;

        .title-span {
            margin-left: 20px
        }

        .line-chart {
            height: 250px;
        }
    }
</style>

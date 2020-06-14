<template>
    <el-card class="chart-card">
        <div slot="header">
            <span><b>内存监控</b></span>
            <span class="title-span">{{data.used|numberFormatter}} / {{data.total|numberFormatter}}</span>
            <span class="title-span">
                当前使用率：
                <count-to
                        :decimals="2"
                        :end-val="data.utilizationRate"
                        :start-val="data.lastUtilizationRate"
                        suffix="%"
                />
            </span>
            <span class="title-span">
                峰值：
                <count-to
                        :decimals="2"
                        :end-val="data.maxUtilizationRate"
                        :start-val="data.lastMaxUtilizationRate"
                        suffix="%"
                />
            </span>
        </div>
        <div class="line-chart"/>
    </el-card>
</template>

<script>
    import {logic, resize} from "@/mixins/chart"
    import CountTo from 'vue-count-to'

    export default {
        name: "Memory",
        mixins: [resize, logic],
        components: {CountTo},
        props: {
            data: Object
        },
        data() {
            return {
                options: {
                    xAxis: {
                        data: [],
                        type: 'category',
                        axisTick: {
                            show: false
                        }
                    },
                    grid: {
                        left: 10,
                        right: 10,
                        bottom: 20,
                        top: 30,
                        containLabel: true
                    },
                    tooltip: {
                        trigger: 'axis',
                        axisPointer: {
                            type: 'cross'
                        },
                        padding: [5, 10]
                    },
                    yAxis: {
                        name: '%',
                        axisTick: {
                            show: false
                        }
                    },
                    dataZoom: [
                        {
                            start: 30,
                            end: 70,
                        }
                    ],
                    series: [{
                        name: '占用率',
                        itemStyle: {
                            normal: {
                                color: '#FF005A',
                                lineStyle: {
                                    color: '#FF005A',
                                    width: 2
                                }
                            }
                        },
                        smooth: true,
                        type: 'line',
                        areaStyle: {},
                        data: []
                    }]
                }
            }
        },
        watch: {
            data: {
                deep: true,
                handler({xData, vData}) {
                    if (!xData || !vData) return
                    this.setOptions({xData, vData})
                }
            }
        },
        methods: {
            init() {
                this.chart.setOption(this.options)
            },
            $_getChartDom() {
                return this.$el.querySelector('.line-chart')
            },
            setOptions({xData, vData} = {}) {
                this.chart.setOption({
                    xAxis: {data: xData},
                    series: [{data: vData}]
                })
            }
        }
    }
</script>

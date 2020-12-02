<template>
    <div :style="{height,width}"/>
</template>

<script>
import {logic, resize} from "@/mixin/chart"
import {getDailyProfitStat} from '@/api/statistic'
import {timeFormat} from "@/util"

export default {
    name: "DailyProfitStat",

    mixins: [resize, logic],

    methods: {
        init() {
            if (this.loading) return
            this.loading = true
            getDailyProfitStat
                .request()
                .then(({data}) => {
                    const time = []
                    const purchase = []
                    const sell = []
                    const profit = []
                    data.forEach(i => {
                        time.push(timeFormat('MM 月 dd日', new Date(i.time)))
                        purchase.push(i.purchase)
                        sell.push(i.sell)
                        profit.push(i.profit)
                    })
                    this.setOptions({time, purchase, sell, profit})
                })
                .finally(() => this.loading = false)
        },

        setOptions({time, purchase, sell, profit}) {
            this.chart.setOption({
                title: {
                    text: '历史七天利润情况统计',
                    left: 'center',
                    align: 'right'
                },
                xAxis: {
                    data: time,
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
                    axisTick: {
                        show: false
                    }
                },
                legend: {
                    data: ['采购额', '销售额', '毛利润'],
                    right: 10
                },
                series: [
                    {
                        name: '采购额',
                        smooth: true,
                        type: 'line',
                        data: purchase
                    },
                    {
                        name: '销售额',
                        smooth: true,
                        type: 'line',
                        data: sell
                    },
                    {
                        name: '毛利润',
                        smooth: true,
                        type: 'line',
                        data: profit
                    }
                ]
            })
        }
    }
}
</script>

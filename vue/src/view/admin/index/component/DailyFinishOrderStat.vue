<template>
    <div :style="{height,width}"/>
</template>

<script>
import {logic, resize} from "@/mixin/chart"
import {getDailyFinishOrder} from '@/api/statistic'
import {timeFormat} from "@/util"

export default {
    name: "DailyFinishOrderStat",

    mixins: [resize, logic],

    methods: {
        init() {
            if (this.loading) return
            this.loading = true
            getDailyFinishOrder
                .request()
                .then(({data}) => {
                    const time = []
                    const purchase = []
                    const sell = []
                    data.forEach(i => {
                        time.push(timeFormat('MM 月 dd日', new Date(i.time)))
                        purchase.push(i.purchase)
                        sell.push(i.sell)
                    })
                    this.setOptions({time, purchase, sell})
                })
                .finally(() => this.loading = false)
        },

        setOptions({time, purchase, sell}) {
            this.chart.setOption({
                title: {
                    text: '历史七天订单完成情况统计',
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
                    data: ['采购订单', '销售订单'],
                    right: 10
                },
                series: [
                    {
                        name: '采购订单',
                        smooth: true,
                        type: 'line',
                        data: purchase
                    },
                    {
                        name: '销售订单',
                        smooth: true,
                        type: 'line',
                        data: sell
                    }
                ]
            })
        }
    }
}
</script>

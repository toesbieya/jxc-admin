<template>
    <div :style="{height,width}"/>
</template>

<script>
    import resize from '@/mixins/chartResizeMixin'
    import logic from '@/mixins/chartLogicMixin'
    import {getDailyFinishOrder} from '@/api/statistic/index'
    import {timeFormat} from "@/utils"

    export default {
        name: "DailyFinishOrderStat",
        mixins: [resize, logic],
        methods: {
            init() {
                if (this.loading) return
                this.loading = true
                getDailyFinishOrder()
                    .then(data => {
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
                        boundaryGap: false,
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
                        data: ['采购额', '销售额'],
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
                        }
                    ]
                })
            }
        }
    }
</script>

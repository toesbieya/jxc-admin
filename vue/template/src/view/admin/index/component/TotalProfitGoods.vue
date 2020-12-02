<template>
    <el-row :gutter="32">
        <el-col v-for="i in ['purchase','sell','profit']" :key="i" :lg="8" :sm="24">
            <el-card style="margin-bottom: 32px;">
                <pie-chart :title="getTitle(i)" :data="getData(i)"/>
            </el-card>
        </el-col>
    </el-row>
</template>

<script>
import PieChart from "@/component/Charts/PieChart"
import {getTotalProfitGoods} from '@/api/statistic'

export default {
    name: "TotalProfitGoods",

    components: {PieChart},

    data() {
        return {
            loading: false,
            purchase: {
                title: '各个商品总采购额',
                data: []
            },
            sell: {
                title: '各个商品总销售额',
                data: []
            },
            profit: {
                title: '各个商品总毛利润',
                data: []
            }
        }
    },

    methods: {
        getTitle(type) {
            return this[type].title
        },

        getData(type) {
            return this[type].data
        },

        init() {
            if (this.loading) return
            this.loading = true
            getTotalProfitGoods
                .request()
                .then(({data}) => {
                    const purchase = []
                    const sell = []
                    const profit = []
                    data.forEach(i => {
                        if (i.purchase) purchase.push({name: i.cname, value: i.purchase})
                        if (i.sell) sell.push({name: i.cname, value: i.sell})
                        if (i.profit) profit.push({name: i.cname, value: i.profit})
                    })
                    this.purchase.data = purchase
                    this.sell.data = sell
                    this.profit.data = profit
                })
                .finally(() => this.loading = false)
        }
    },

    mounted() {
        this.init()
    }
}
</script>

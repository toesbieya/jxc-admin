<template>
    <div>
        <panel-group/>

        <el-card>
            <daily-profit-stat/>
        </el-card>

        <el-card style="margin: 32px 0">
            <daily-finish-order-stat/>
        </el-card>

        <total-profit-goods/>
    </div>
</template>

<script>
import PanelGroup from './components/PanelGroup'
import DailyProfitStat from "./components/DailyProfitStat"
import DailyFinishOrderStat from "./components/DailyFinishOrderStat"
import TotalProfitGoods from "./components/TotalProfitGoods"

export default {
    name: 'index',

    components: {PanelGroup, DailyProfitStat, DailyFinishOrderStat, TotalProfitGoods}
}
</script>

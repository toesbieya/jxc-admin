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
import PanelGroup from './component/PanelGroup'
import DailyProfitStat from "./component/DailyProfitStat"
import DailyFinishOrderStat from "./component/DailyFinishOrderStat"
import TotalProfitGoods from "./component/TotalProfitGoods"

export default {
    name: 'index',

    components: {PanelGroup, DailyProfitStat, DailyFinishOrderStat, TotalProfitGoods}
}
</script>

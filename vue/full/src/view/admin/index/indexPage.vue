<script type="text/jsx">
import PanelGroup from './component/PanelGroup'
import DailyProfitStat from "./component/DailyProfitStat"
import DailyFinishOrderStat from "./component/DailyFinishOrderStat"
import TotalProfitGoods from "./component/TotalProfitGoods"

export default {
    name: 'index',

    render() {
        return (
            <div>
                <PanelGroup/>

                <el-card>
                    <DailyProfitStat/>
                </el-card>

                <el-card style="margin: 32px 0">
                    <DailyFinishOrderStat/>
                </el-card>

                <TotalProfitGoods/>
            </div>
        )
    }
}
</script>

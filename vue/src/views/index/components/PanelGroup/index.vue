<template>
    <el-row :gutter="40" class="panel-group">
        <el-col :xs="12" :sm="12" :lg="6" @click.native="jump('/system/user')">
            <panel-group-item icon="user" :color="variables.info" :value="data.online" text="在线用户"/>
        </el-col>
        <el-col :xs="12" :sm="12" :lg="6" @click.native="jump('/purchase/order')">
            <panel-group-item icon="shopping" :color="variables.primary" :value="data.purchase" text="今日采购额"/>
        </el-col>
        <el-col :xs="12" :sm="12" :lg="6" @click.native="jump('/sell/order')">
            <panel-group-item icon="sell" :color="variables.danger" :value="data.sell" text="今日销售额"/>
        </el-col>
        <el-col :xs="12" :sm="12" :lg="6">
            <panel-group-item icon="money" :color="variables.success" :value="data.profit" text="今日毛利润"/>
        </el-col>
    </el-row>
</template>

<script type="text/jsx">
    import PanelGroupItem from './PanelGroupItem'
    import variables from '@/assets/styles/variables.scss'
    import {getFourBlock} from '@/api/statistic/index'
    import {mergeObj} from "@/utils"
    import {auth} from "@/utils/auth"

    export default {
        name: 'panelGroup',
        components: {PanelGroupItem},
        data() {
            return {
                loading: false,
                variables,
                data: {
                    online: 0,
                    purchase: 0,
                    sell: 0,
                    profit: 0
                }
            }
        },
        methods: {
            init() {
                if (this.loading) return
                this.loading = true
                getFourBlock()
                    .then(data => mergeObj(this.data, data))
                    .finally(() => this.loading = false)
            },
            jump(path) {
                auth(path) && this.$router.push(path)
            }
        },
        mounted() {
            this.init()
        }
    }
</script>

<style lang="scss">
    .panel-group {
        padding-top: 17px;

        > .el-col {
            margin-bottom: 32px;
        }
    }
</style>

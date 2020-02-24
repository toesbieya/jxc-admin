<template>
    <el-row :gutter="40" class="panel-group">
        <el-col v-for="i in list" :key="i.id" :xs="12" :sm="12" :lg="6">
            <panel-group-item :ref="i.id" v-bind="i" @click.native="jump(i)"/>
        </el-col>
    </el-row>
</template>

<script type="text/jsx">
    import PanelGroupItem from './PanelGroupItem'
    import variables from '@/assets/styles/variables.scss'
    import {getFourBlock} from '@/api/statistic/index'
    import {isEmpty, mergeObj} from "@/utils"
    import {auth} from "@/utils/auth"

    export default {
        name: 'panelGroup',
        components: {PanelGroupItem},
        data() {
            return {
                loading: false,
                list: [
                    {id: 'online', path: '/system/user', icon: 'user', color: variables.info, value: 0, text: '在线用户'},
                    {
                        id: 'purchase',
                        path: '/purchase/order',
                        icon: 'shopping',
                        color: variables.primary,
                        value: 0,
                        text: '今日采购额'
                    },
                    {id: 'sell', path: '/sell/order', icon: 'sell', color: variables.danger, value: 0, text: '今日销售额'},
                    {id: 'profit', icon: 'money', color: variables.info, value: 0, text: '今日毛利润'},
                ]
            }
        },
        methods: {
            init() {
                if (this.loading) return
                this.loading = true
                getFourBlock()
                    .then(data => {
                        this.list.forEach(i => {
                            if (i.id in data) i.value = data[i.id]
                        })
                    })
                    .finally(() => this.loading = false)
            },
            jump({id, path}) {
                if (!isEmpty(path) && auth(path)) {
                    this.$router.push(path)
                    this.$refs[id][0].mouseout()
                }
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

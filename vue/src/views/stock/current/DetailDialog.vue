<template>
    <dialog-form :value="value" :loading="false" :title="title" width="70%" @close="cancel" @open="search">
        <liner-progress :show="loading"/>

        <abstract-table :data="tableData" border show-summary :summary-method="summary" :span-method="span">
            <el-table-column align="center" label="采购订单" show-overflow-tooltip>
                <router-link
                        slot-scope="{row}"
                        :to="{name:'purchaseOrder',params:{type:'see',id:row.cgddid}}"
                        @click.native="cancel"
                >
                    {{row.cgddid}}
                </router-link>
            </el-table-column>
            <el-table-column align="center" label="采购单价" prop="price" show-overflow-tooltip/>
            <el-table-column align="center" label="采购入库单" show-overflow-tooltip>
                <router-link
                        slot-scope="{row}"
                        :to="{name:'purchaseInbound',params:{type:'see',id:row.cgrkid}}"
                        @click.native="cancel"
                >
                    {{row.cgrkid}}
                </router-link>
            </el-table-column>
            <el-table-column align="center" label="入库时间" show-overflow-tooltip>
                <template v-slot="{row}">{{row.ctime | timestamp2Date}}</template>
            </el-table-column>
            <el-table-column align="center" label="库存数量" prop="num" show-overflow-tooltip/>
            <el-table-column align="center" label="总 值" prop="total" show-overflow-tooltip/>
        </abstract-table>
    </dialog-form>
</template>

<script>
    import AbstractTable from '@/components/AbstractTable'
    import LinerProgress from '@/components/LinerProgress'
    import DialogForm from "@/components/DialogForm"
    import dialogMixin from "@/mixins/dialogMixin"
    import {isEmpty} from "@/utils"
    import {plus, mul} from "@/utils/math"
    import {getDetail} from "@/api/stock/current"

    export default {
        name: "DetailDialog",

        mixins: [dialogMixin],

        components: {AbstractTable, LinerProgress, DialogForm},

        props: {value: Boolean, title: String, cid: Number},

        data() {
            return {
                loading: false,
                tableData: []
            }
        },

        methods: {
            summary({data}) {
                let sum = ['合计', '', '', '', 0, 0]
                data.forEach(i => {
                    sum[4] = plus(sum[4], i.num)
                    sum[5] = plus(sum[5], i.total)
                })
                return sum
            },

            span({row, column, rowIndex, columnIndex}) {
                if (columnIndex === 0 || columnIndex === 1) {
                    return row._span
                }
            },

            search() {
                this.tableData = []
                if (!this.value || isEmpty(this.cid) || this.loading) return
                this.loading = true
                getDetail(this.cid)
                    .then(data => {
                        this.prepareData(data)
                        this.tableData = data
                    })
                    .finally(() => this.loading = false)
            },

            prepareData(data) {
                data.sort((a, b) => {
                    if (a.cgddid > b.cgddid) return -1
                    if (a.cgddid < b.cgddid) return 1
                    return 0
                })
                let count = {}
                data.forEach(i => {
                    i.total = mul(i.num, i.price)
                    i._span = {rowspan: 0, colspan: 0}
                    count[i.cgddid] ? count[i.cgddid]++ : count[i.cgddid] = 1
                })
                for (let i = 0; i < data.length; i++) {
                    let num = count[data[i].cgddid]
                    if (num === 1) {
                        data[i]._span = {rowspan: 1, colspan: 1}
                    }
                    if (num > 1) {
                        data[i]._span = {rowspan: num, colspan: 1}
                        i += num - 1
                    }
                }
            },

            cancel() {
                this.closeDialog()
                this.tableData = []
            }
        }
    }
</script>

<style lang="scss" scoped>
    @import "~@/assets/styles/variables.scss";

    .el-table {
        a {
            color: $--color-primary;
        }
    }
</style>

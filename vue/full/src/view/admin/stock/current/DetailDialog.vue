<template>
    <abstract-dialog :value="value" :loading="false" :title="title" width="70%" @close="cancel" @open="search">
        <liner-progress :show="loading"/>

        <abstract-table
            :data="tableData"
            :highlight-current-row="false"
            border
            show-summary
            :summary-method="summary"
            :span-method="span"
        >
            <el-table-column align="center" label="采购订单" show-overflow-tooltip>
                <router-link
                    slot-scope="{row}"
                    :to="`/purchase/order/detail/see/${row.cgddid}`"
                    @click.native="cancel"
                >
                    {{ row.cgddid }}
                </router-link>
            </el-table-column>
            <el-table-column align="center" label="采购单价" prop="price" show-overflow-tooltip/>
            <el-table-column align="center" label="采购入库单" show-overflow-tooltip>
                <router-link
                    slot-scope="{row}"
                    :to="`/purchase/inbound/detail/see/${row.cgrkid}`"
                    @click.native="cancel"
                >
                    {{ row.cgrkid }}
                </router-link>
            </el-table-column>
            <el-table-column align="center" label="入库时间" show-overflow-tooltip>
                <template v-slot="{row}">{{ row.ctime | timestamp2Date }}</template>
            </el-table-column>
            <el-table-column align="center" label="库存数量" prop="num" show-overflow-tooltip/>
            <el-table-column align="center" label="总 值" prop="total" show-overflow-tooltip/>
        </abstract-table>
    </abstract-dialog>
</template>

<script>
import AbstractTable from '@/component/abstract/Table'
import LinerProgress from '@/component/LinerProgress'
import AbstractDialog from "@/component/abstract/Dialog"
import dialogMixin from "@/mixin/dialogMixin"
import {isEmpty} from "@/util"
import {plus, mul} from "@/util/math"
import {getDetail} from "@/api/stock/current"

export default {
    name: "DetailDialog",

    mixins: [dialogMixin],

    components: {AbstractTable, LinerProgress, AbstractDialog},

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
            getDetail
                .request(this.cid)
                .then(({data}) => {
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
            const count = {}
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

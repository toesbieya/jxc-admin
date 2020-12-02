<template>
    <el-dialog
        v-drag-dialog
        :visible="value"
        append-to-body
        title="选择库存商品"
        width="70%"
        @close="cancel"
        @open="search"
    >
        <el-scrollbar>
            <el-row>
                <el-button icon="el-icon-search" size="small" type="primary" @click="search">查 询</el-button>
                <el-button size="small" type="primary" @click="confirm">确 定</el-button>
                <el-button plain size="small" @click="closeDialog">取 消</el-button>
            </el-row>

            <el-row class="table-container">
                <liner-progress :show="config.loading"/>

                <el-table
                    ref="table"
                    :data="tableData"
                    current-row-key="id"
                    row-key="id"
                    @selection-change="selection=$event"
                >
                    <el-table-column align="center" type="selection" reserve-selection width="80"/>
                    <el-table-column align="center" label="商品分类" prop="cname" show-overflow-tooltip/>
                    <el-table-column align="center" label="库存数量" prop="totalNum" show-overflow-tooltip/>
                </el-table>

                <abstract-pagination :model="searchForm" @current-change="pageChange"/>
            </el-row>
        </el-scrollbar>
    </el-dialog>
</template>

<script>
import dialogMixin from "@/mixin/dialogMixin"
import tableMixin from '@/mixin/tablePageMixin'
import LinerProgress from '@/component/LinerProgress'
import {search} from "@/api/stock/current"
import {elError} from "@/util/message"

export default {
    name: "StockSelector",

    mixins: [dialogMixin, tableMixin],

    components: {LinerProgress},

    props: {value: Boolean},

    data() {
        return {
            selection: []
        }
    },

    methods: {
        search() {
            if (!this.value || this.config.loading) return
            this.config.loading = true
            this.$refs.table && this.$refs.table.clearSelection()
            search
                .request(this.searchForm)
                .then(({data: {list, total}}) => {
                    this.searchForm.total = total
                    this.tableData = list
                })
                .finally(() => this.config.loading = false)
        },

        confirm() {
            if (this.selection.length <= 0) return elError('请选择商品')
            this.$emit('select', this.selection)
        },

        cancel() {
            this.closeDialog()
            this.$refs.table.clearSelection()
            this.tableData = []
        }
    }
}
</script>

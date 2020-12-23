<template>
    <el-dialog
        v-drag-dialog
        :visible="value"
        append-to-body
        title="选择采购订单"
        width="70%"
        @close="cancel"
        @open="search"
    >
        <el-scrollbar>
            <el-row>
                <el-button icon="el-icon-search" size="small" type="primary" @click="search">查 询</el-button>
                <el-button plain size="small" @click="closeDialog">取 消</el-button>
            </el-row>

            <el-row v-loading="config.loading" class="table-container">
                <abstract-table :data="tableData" :highlight-current-row="false" @expand-change="getSubList">
                    <el-table-column align="center" type="expand">
                        <template v-slot="{row}">
                            <liner-progress :show="row._loading"/>
                            <div v-show="!row._loading" style="text-align: center">
                                <el-table
                                    :data="row.data"
                                    row-key="id"
                                    border
                                    @selection-change="row._selection = $event"
                                >
                                    <el-table-column
                                        :selectable="p=>p.remainNum > 0"
                                        align="center"
                                        type="selection"
                                        width="60"
                                    />
                                    <el-table-column align="center" label="#" type="index" width="80"/>
                                    <el-table-column align="center" label="商品" prop="cname" show-overflow-tooltip/>
                                    <el-table-column align="center" label="采购数量" prop="num"/>
                                    <el-table-column align="center" label="未入库数量" prop="remainNum"/>
                                </el-table>
                                <el-button
                                    v-if="row.data && row.data.length > 0"
                                    size="small"
                                    style="margin-top: 10px"
                                    type="primary"
                                    @click="confirm(row)"
                                >
                                    确定
                                </el-button>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column align="center" label="#" type="index" width="80"/>
                    <el-table-column align="center" label="单号" prop="id" show-overflow-tooltip/>
                    <el-table-column align="center" label="创建人" prop="cname" show-overflow-tooltip/>
                    <el-table-column align="center" label="创建时间" show-overflow-tooltip>
                        <template v-slot="{row}">{{ row.ctime | timestamp2Date }}</template>
                    </el-table-column>
                    <el-table-column align="center" label="审核人" prop="vname" show-overflow-tooltip/>
                    <el-table-column align="center" label="审核时间" show-overflow-tooltip>
                        <template v-slot="{row}">{{ row.vtime | timestamp2Date }}</template>
                    </el-table-column>
                </abstract-table>
            </el-row>
        </el-scrollbar>
    </el-dialog>
</template>

<script>
import LinerProgress from '@/component/LinerProgress'
import dialogMixin from "@/mixin/dialogMixin"
import tableMixin from '@/mixin/tablePageMixin'
import {getSubById, search} from "@/api/doc/purchase/order"
import {elError} from "@/util/message"

export default {
    name: "OrderSelector",

    mixins: [dialogMixin, tableMixin],

    components: {LinerProgress},

    props: {value: Boolean},

    data() {
        return {
            searchForm: {
                status: 2,
                finish: '0,1'
            }
        }
    },

    methods: {
        search() {
            if (!this.value || this.config.loading) return
            this.config.loading = true
            this.row = null
            search
                .request(this.searchForm)
                .then(({data: {list, total}}) => {
                    list.forEach(i => {
                        i._loading = false //加载状态
                        i._loaded = false //是否已经加载完成
                        i._selection = [] //选中的
                    })
                    this.searchForm.total = total
                    this.tableData = list
                })
                .finally(() => this.config.loading = false)
        },

        getSubList(row) {
            if (row._loaded || row._loading) return
            row._loading = true
            getSubById
                .request(row.id)
                .then(({data}) => {
                    row.data = data
                    row._loaded = true
                })
                .finally(() => row._loading = false)
        },

        confirm(row) {
            if (row._selection.length <= 0) return elError('请选择要入库的商品')
            this.$emit('select', row.id, row._selection)
            this.cancel()
        },

        cancel() {
            this.closeDialog()
            this.tableData = []
        }
    }
}
</script>

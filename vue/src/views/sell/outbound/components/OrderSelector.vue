<template>
    <el-dialog
            v-drag-dialog
            :visible="value"
            append-to-body
            title="选择销售订单"
            width="50%"
            top="50px"
            @close="cancel"
            @open="search"
    >
        <el-scrollbar>
            <el-row>
                <el-button icon="el-icon-search" size="small" type="success" @click="search">查 询</el-button>
                <el-button plain size="small" @click="cancel">取 消</el-button>
            </el-row>
            <el-table
                    v-loading="config.loading"
                    :data="tableData"
                    class="table-container"
                    @expand-change="getSubList"
            >
                <el-table-column align="center" type="expand">
                    <template v-slot="{row}">
                        <liner-progress :show="row._loading"/>
                        <div style="text-align: center" v-show="!row._loading">
                            <el-table
                                    :data="row.data"
                                    row-key="id"
                                    border
                                    @selection-change="row._selection=$event"
                            >
                                <el-table-column
                                        :selectable="p=>p.remain_num>0"
                                        align="center"
                                        type="selection"
                                        width="60"
                                />
                                <el-table-column align="center" label="#" type="index" width="80"/>
                                <el-table-column align="center" label="商品" prop="cname" show-overflow-tooltip/>
                                <el-table-column align="center" label="销售数量" prop="num"/>
                                <el-table-column align="center" label="未出库数量" prop="remain_num"/>
                            </el-table>
                            <el-button
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
                    <template v-slot="{row}">{{row.ctime | timestamp2Date}}</template>
                </el-table-column>
                <el-table-column align="center" label="审核人" prop="vname" show-overflow-tooltip/>
                <el-table-column align="center" label="审核时间" show-overflow-tooltip>
                    <template v-slot="{row}">{{row.vtime | timestamp2Date}}</template>
                </el-table-column>
            </el-table>
        </el-scrollbar>
    </el-dialog>
</template>

<script>
    import LinerProgress from '@/components/LinerProgress'
    import tableMixin from '@/mixins/tablePageMixin'
    import {getSubById, search} from "@/api/sell/order"
    import {elError} from "@/utils/message"

    export default {
        name: "OrderSelector",
        mixins: [tableMixin],
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
                search(this.searchForm)
                    .then(({list, total}) => {
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
                getSubById(row.id)
                    .then(data => {
                        row.data = data
                        row._loaded = true
                    })
                    .finally(() => row._loading = false)
            },
            confirm(row) {
                if (row._selection.length <= 0) return elError('请选择要出库的商品')
                this.$emit('select', row.id, row._selection)
                this.cancel()
            },
            cancel() {
                this.$emit('input', false)
                this.tableData = []
            }
        }
    }
</script>
<style lang="scss" scoped>
    .el-table {
        .nets-table-btn {
            margin: 0 auto;
        }
    }
</style>

<template>
    <el-dialog
            v-drag-dialog
            :visible="value"
            append-to-body
            title="选择供应商"
            width="70%"
            top="50px"
            @close="cancel"
            @open="search"
    >
        <el-row>
            <el-button icon="el-icon-search" size="small" type="success" @click="search">查 询</el-button>
            <el-button size="small" type="primary" @click="confirm">确 定</el-button>
            <el-button plain size="small" @click="closeDialog">取 消</el-button>
            <el-button
                    v-if="canGoToSysSupplierPage"
                    plain
                    size="small"
                    type="dashed"
                    @click="goToSysSupplierPage"
            >
                添加供应商
            </el-button>
        </el-row>
        <el-scrollbar>
            <el-row class="table-container">
                <liner-progress :show="config.loading"/>
                <el-table
                        ref="table"
                        :data="tableData"
                        current-row-key="id"
                        highlight-current-row
                        row-key="id"
                        @row-click="rowClick"
                        @row-dblclick="dbclick"
                >
                    <el-table-column align="center" label="#" type="index" width="80"/>
                    <el-table-column align="center" label="供应商" prop="name" show-overflow-tooltip/>
                    <el-table-column align="center" label="行政区域" prop="region_name" show-overflow-tooltip/>
                    <el-table-column align="center" label="地 址" prop="address" show-overflow-tooltip/>
                    <el-table-column align="center" label="联系人" prop="linkman" show-overflow-tooltip/>
                    <el-table-column align="center" label="联系电话" prop="linkphone" show-overflow-tooltip/>
                    <el-table-column align="center" label="创建时间" width="150" show-overflow-tooltip>
                        <template v-slot="{row}">{{row.ctime | timestamp2Date}}</template>
                    </el-table-column>
                    <el-table-column align="center" label="状 态" width="120">
                        <template v-slot="{row}">
                            <span :class="row.status===1?'success':'error'" class="dot"/>
                            <span>{{row.status===1?'启用':'禁用'}}</span>
                        </template>
                    </el-table-column>
                </el-table>
                <el-pagination
                        background
                        :current-page="searchForm.page"
                        :page-size="searchForm.pageSize"
                        :total="searchForm.total"
                        layout="total, prev, pager, next, jumper"
                        @current-change="pageChange"
                />
            </el-row>
        </el-scrollbar>
    </el-dialog>
</template>

<script>
    import LinerProgress from '@/components/LinerProgress'
    import dialogMixin from "@/mixins/dialogMixin"
    import tableMixin from '@/mixins/tablePageMixin'
    import {getSuppliers} from "@/api/system/supplier"
    import {elError} from "@/utils/message"
    import {auth} from "@/utils/auth"

    export default {
        name: "SupplierSelector",
        mixins: [dialogMixin, tableMixin],
        components: {LinerProgress},
        props: {value: Boolean},
        data() {
            return {
                searchForm: {
                    status: 1
                }
            }
        },
        computed: {
            canGoToSysSupplierPage() {
                return auth('/system/supplier')
            }
        },
        methods: {
            search() {
                if (!this.value || this.config.loading) return
                this.config.loading = true
                this.row = null
                getSuppliers(this.searchForm)
                    .then(({list, total}) => {
                        this.searchForm.total = total
                        this.tableData = list
                    })
                    .finally(() => this.config.loading = false)
            },
            dbclick(row) {
                this.row = row
                this.confirm()
            },
            confirm() {
                if (!this.row) return elError('请选择一个供应商')
                this.$emit('select', this.row)
                this.cancel()
            },
            cancel() {
                this.closeDialog()
                this.tableData = []
            },
            goToSysSupplierPage() {
                this.closeDialog()
                this.$emit('jump')
                this.$nextTick(() => this.$router.push('/system/supplier'))
            }
        }
    }
</script>

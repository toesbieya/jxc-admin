<template>
    <el-row>
        <el-card class="card-container max-view-height" style="width: 250px;float: left;margin-right: 15px">
            <el-input
                    v-model="temp.filter"
                    size="small"
                    placeholder="筛选分类"
                    clearable
                    style="margin-bottom: 10px"
                    @input="filter"
            />
            <category-tree ref="tree" :filter-node-method="filterNode" @node-click="nodeClick"/>
        </el-card>

        <el-card>
            <search-form>
                <search-form-item :sm="24" :lg="12" :xl="8" label="商品分类：">
                    <el-input :value="temp.cname" maxlength="100" clearable @clear="clearCidSearch"/>
                </search-form-item>
                <search-form-item :sm="24" :lg="12" :xl="8" label="采购订单：">
                    <el-input v-model="searchForm.cgddid" maxlength="100" clearable/>
                </search-form-item>
                <search-form-item :sm="24" :lg="12" :xl="8" label="采购入库单：">
                    <el-input v-model="searchForm.cgrkid" maxlength="100" clearable/>
                </search-form-item>
                <search-form-item :sm="24" :lg="12" :xl="8" label="入库时间：">
                    <el-date-picker
                            v-model="temp.ctime"
                            format="yyyy-MM-dd"
                            range-separator="-"
                            type="daterange"
                            value-format="timestamp"
                    />
                </search-form-item>
            </search-form>
            <el-row class="button-group">
                <el-button icon="el-icon-search" size="small" type="success" @click="search">查 询</el-button>
                <el-button icon="el-icon-download" size="small" type="info" @click="downloadExcel">导 出</el-button>
            </el-row>
            <el-row v-loading="config.loading" class="table-container">
                <el-table
                        ref="table"
                        :data="tableData"
                        current-row-key="id"
                        row-key="id"
                        show-summary
                        :summary-method="summary"
                >
                    <el-table-column align="center" label="#" type="index" width="80"/>
                    <el-table-column align="center" label="商品分类" prop="cname" show-overflow-tooltip/>
                    <el-table-column align="center" label="库存数量" prop="total_num" show-overflow-tooltip/>
                    <el-table-column align="center" label="总 值" prop="total_price" show-overflow-tooltip/>
                    <el-table-column align="center" label="操作">
                        <template v-slot="{row}">
                            <el-button size="small" type="primary" @click="more(row)">详细</el-button>
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
        </el-card>

        <detail-dialog v-model="detailDialog" :title="title" :cid="cid"/>
    </el-row>
</template>

<script>
    import SearchForm from "@/bizComponents/SearchForm"
    import SearchFormItem from "@/bizComponents/SearchForm/SearchFormItem"
    import CategoryTree from '@/bizComponents/CategoryTree'
    import DetailDialog from "./DetailDialog"
    import {search} from "@/api/stock/current"
    import {debounce, isEmpty, plus} from "@/utils"
    import {exportExcel} from "@/utils/excel"
    import {getNodeId} from "@/utils/tree"
    import tableMixin from '@/mixins/tablePageMixin'

    const baseUrl = '/stock/current'

    export default {
        name: "currentStock",
        mixins: [tableMixin],
        components: {SearchForm, SearchFormItem, CategoryTree, DetailDialog},
        data() {
            return {
                searchForm: {
                    cids: null,
                    cgddids: null,
                    cgrkids: null
                },
                temp: {
                    filter: '',
                    cname: null,
                    cids: null,
                    ctime: []
                },
                detailDialog: false,
                excel: {
                    column: [
                        {header: '序号', prop: 'id', width: 20, merge: true},
                        {header: '分类名称', prop: 'cname', width: 20, merge: true},
                        {header: '库存总数', prop: 'total_num', width: 20, merge: true},
                        {header: '库存总值', prop: 'total_price', width: 20, merge: true},
                        {header: '采购订单号', prop: 'cgddid', width: 20, merge: true},
                        {header: '采购单价', prop: 'cg_price', width: 20, merge: true},
                        {header: '采购数量', prop: 'cg_num', width: 20, merge: true},
                        {header: '采购入库单号', prop: 'cgrkid', width: 20},
                        {header: '入库时间', prop: 'ctime', width: 20},
                        {header: '入库数量', prop: 'rk_num', width: 20},
                    ],
                    merge: {primaryKey: 'cid', orderKey: 'id'}
                }
            }
        },
        computed: {
            title() {
                return this.row ? `库存详细(${this.row.cname})` : ''
            },
            cid() {
                return this.row ? this.row.cid : null
            }
        },
        methods: {
            summary({data}) {
                let sum = ['合计', '', '', 0]
                data.forEach(i => {
                    sum[3] = plus(sum[3], i.total_price)
                })
                return sum
            },
            filterNode(value, data) {
                if (isEmpty(value)) return true
                return data.name.includes(value)
            },
            filter(v) {
                this.$refs.tree.$refs.tree.filter(v)
            },
            mergeSearchForm() {
                return {
                    ...this.searchForm,
                    startTime: this.temp.ctime ? this.temp.ctime[0] : null,
                    endTime: this.temp.ctime ? this.temp.ctime[1] + 86400000 : null
                }
            },
            search() {
                if (this.config.loading) return
                this.config.loading = true
                search(this.mergeSearchForm())
                    .then(({list, total}) => {
                        this.searchForm.total = total
                        this.tableData = list
                    })
                    .finally(() => this.config.loading = false)
            },
            downloadExcel() {
                exportExcel(baseUrl + '/export', this.mergeSearchForm(), this.excel)
            },
            nodeClick(obj) {
                let ids = getNodeId(obj.children)
                ids.unshift(obj.id)
                this.searchForm.cids = ids.join(',')
                this.temp.cname = obj.name
            },
            clearCidSearch() {
                this.searchForm.cids = null
                this.temp.cname = null
                let tree = this.$refs.tree.$refs.tree
                tree.setCurrentKey()
            },
            more(row) {
                this.row = row
                this.detailDialog = true
            }
        },
        created() {
            this.filter = debounce(this.filter, 500)
        }
    }
</script>

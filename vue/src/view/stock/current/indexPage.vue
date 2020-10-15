<template>
    <el-card>
        <extra-area>
            <template v-slot:extra>
                <el-input
                    v-model="temp.filter"
                    size="small"
                    placeholder="筛选分类"
                    clearable
                    suffix-icon="el-icon-search"
                    style="margin-bottom: 10px"
                    @input="filter"
                />
                <category-tree ref="tree" :filter-node-method="filterNode" @node-click="nodeClick"/>
            </template>

            <search-form v-model="searchForm" @search="search" @reset="clearCidSearch">
                <el-form-item label="商品分类">
                    <el-input :value="temp.cname" maxlength="100" clearable @clear="clearCidSearch"/>
                </el-form-item>
                <el-form-item label="采购订单">
                    <el-input v-model="searchForm.cgddid" maxlength="100" clearable/>
                </el-form-item>
                <el-form-item label="采购入库单">
                    <el-input v-model="searchForm.cgrkid" maxlength="100" clearable/>
                </el-form-item>
                <el-form-item label="入库时间">
                    <el-date-picker
                        v-model="temp.ctime"
                        format="yyyy-MM-dd"
                        range-separator="-"
                        type="daterange"
                        value-format="timestamp"
                    />
                </el-form-item>
            </search-form>

            <el-row class="button-group">
                <el-button icon="el-icon-download" size="small" type="primary" @click="downloadExcel">导 出</el-button>
            </el-row>

            <el-row v-loading="config.loading" class="table-container">
                <abstract-table
                    :data="tableData"
                    :highlight-current-row="false"
                    show-summary
                    :summary-method="summary"
                >
                    <el-table-column align="center" label="#" type="index" width="80"/>
                    <el-table-column align="center" label="商品分类">
                        <a slot-scope="{row}" type="primary" @click="() => more(row)">{{ row.cname }}</a>
                    </el-table-column>
                    <el-table-column align="center" label="库存数量" prop="totalNum" show-overflow-tooltip/>
                    <el-table-column align="center" label="总 值" prop="totalPrice" show-overflow-tooltip/>
                </abstract-table>

                <abstract-pagination :model="searchForm" @current-change="pageChange"/>
            </el-row>
        </extra-area>

        <detail-dialog v-model="detailDialog" :title="title" :cid="cid"/>
    </el-card>
</template>

<script>
import tableMixin from '@/mixin/tablePageMixin'
import CategoryTree from '@/component/biz/CategoryTree'
import DetailDialog from "./DetailDialog"
import ExtraArea from '@/component/ExtraArea'
import SearchForm from "@/component/form/Search"
import {search} from "@/api/stock/current"
import {isEmpty, debounce} from "@/util"
import {exportExcel} from "@/util/excel"
import {plus} from "@/util/math"
import {getNodeId} from "@/util/tree"

export default {
    name: "currentStock",

    mixins: [tableMixin],

    components: {CategoryTree, DetailDialog, ExtraArea, SearchForm},

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
                columns: [
                    {header: '序号', prop: 'id', width: 20, merge: true},
                    {header: '分类名称', prop: 'cname', width: 20, merge: true},
                    {header: '库存总数', prop: 'totalNum', width: 20, merge: true},
                    {header: '库存总值', prop: 'totalPrice', width: 20, merge: true},
                    {header: '采购订单号', prop: 'cgddid', width: 20, merge: true},
                    {header: '采购单价', prop: 'cgPrice', width: 20, merge: true},
                    {header: '采购数量', prop: 'cgNum', width: 20, merge: true},
                    {header: '采购入库单号', prop: 'cgrkid', width: 20},
                    {header: '入库时间', prop: 'ctime', width: 20},
                    {header: '入库数量', prop: 'rkNum', width: 20},
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
                sum[3] = plus(sum[3], i.totalPrice)
            })
            return sum
        },

        filterNode(value, data) {
            if (isEmpty(value)) return true
            return data.name.includes(value)
        },

        filter(v) {
            this.$refs.tree.filter(v)
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
            search
                .request(this.mergeSearchForm())
                .then(({data: {list, total}}) => {
                    this.searchForm.total = total
                    this.tableData = list
                })
                .finally(() => this.config.loading = false)
        },

        downloadExcel() {
            exportExcel(`/stock/current/export`, this.mergeSearchForm(), this.excel)
        },

        nodeClick(obj) {
            const ids = getNodeId(obj.children)
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

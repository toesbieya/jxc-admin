<template>
    <el-card v-loading="config.operating">
        <search-form>
            <search-form-item label="单 号：">
                <el-input v-model="searchForm.id_fuzzy" clearable maxlength="50"/>
            </search-form-item>
            <search-form-item label="供应商：">
                <el-input v-model="searchForm.sname" clearable maxlength="50"/>
            </search-form-item>
            <search-form-item label="创建人：">
                <el-input v-model="searchForm.cname" clearable maxlength="50"/>
            </search-form-item>
            <search-form-item label="创建时间：">
                <el-date-picker
                        v-model="temp.ctime"
                        format="yyyy-MM-dd"
                        range-separator="-"
                        type="daterange"
                        value-format="timestamp"
                />
            </search-form-item>
            <search-form-item label="审核人：">
                <el-input v-model="searchForm.vname" clearable maxlength="50"/>
            </search-form-item>
            <search-form-item label="审核时间：">
                <el-date-picker
                        v-model="temp.vtime"
                        format="yyyy-MM-dd"
                        range-separator="-"
                        type="daterange"
                        value-format="timestamp"
                />
            </search-form-item>
            <search-form-item label="状 态：">
                <el-select v-model="temp.status" clearable multiple>
                    <el-option :value="0" label="拟定"/>
                    <el-option :value="1" label="待审核"/>
                    <el-option :value="2" label="已审核"/>
                </el-select>
            </search-form-item>
            <search-form-item label="完成情况：">
                <el-select v-model="temp.finish" clearable multiple>
                    <el-option :value="0" label="未开始"/>
                    <el-option :value="1" label="进行中"/>
                    <el-option :value="2" label="已完成"/>
                </el-select>
            </search-form-item>
            <search-form-item label="结束时间：">
                <el-date-picker
                        v-model="temp.ftime"
                        format="yyyy-MM-dd"
                        range-separator="-"
                        type="daterange"
                        value-format="timestamp"
                />
            </search-form-item>
        </search-form>
        <el-row>
            <el-button icon="el-icon-search" size="small" type="success" @click="search">查 询</el-button>
            <el-button v-if="canAdd" icon="el-icon-plus" size="small" type="primary" @click="add">添 加</el-button>
            <el-button icon="el-icon-view" size="small" type="primary" @click="see">查 看</el-button>
            <el-button v-if="canUpdate" icon="el-icon-edit" size="small" type="primary" @click="edit">编 辑</el-button>
            <el-button v-if="canDel" icon="el-icon-delete" size="small" type="danger" @click="del">删 除</el-button>
            <el-button v-if="canExport" icon="el-icon-download" size="small" type="info" @click="downloadExcel">导 出
            </el-button>
        </el-row>
        <el-row v-loading="config.loading" class="table-container">
            <el-table
                    ref="table"
                    :data="tableData"
                    current-row-key="id"
                    highlight-current-row
                    row-key="id"
                    @row-click="row=$event"
                    @expand-change="getSubList"
            >
                <el-table-column align="center" type="expand">
                    <template v-slot="{row}">
                        <liner-progress :show="row._loading"/>
                        <el-table v-show="!row._loading" :data="row.data" border row-key="id">
                            <el-table-column align="center" label="#" type="index" width="80"/>
                            <el-table-column align="center" label="商品" prop="cname" show-overflow-tooltip/>
                            <el-table-column align="center" label="采购数量" prop="num"/>
                            <el-table-column align="center" label="采购单价" prop="price"/>
                            <el-table-column align="center" label="未入库数量" prop="remain_num"/>
                        </el-table>
                    </template>
                </el-table-column>
                <el-table-column align="center" label="#" type="index" width="80"/>
                <el-table-column align="center" label="单 号" prop="id" width="160" show-overflow-tooltip/>
                <el-table-column align="center" label="供应商" prop="sname" show-overflow-tooltip/>
                <el-table-column align="center" label="创建人" prop="cname" show-overflow-tooltip/>
                <el-table-column align="center" label="创建时间" width="150" show-overflow-tooltip>
                    <template v-slot="{row}">{{row.ctime | timestamp2Date}}</template>
                </el-table-column>
                <el-table-column align="center" label="审核人" prop="vname" show-overflow-tooltip/>
                <el-table-column align="center" label="审核时间" width="150" show-overflow-tooltip>
                    <template v-slot="{row}">{{row.vtime | timestamp2Date}}</template>
                </el-table-column>
                <el-table-column align="center" label="状 态" width="120">
                    <template v-slot="{row}">
                        <span :class="{primary:row.status===1,success:row.status===2}" class="dot"/>
                        {{getStatus(row.status)}}
                    </template>
                </el-table-column>
                <el-table-column align="center" label="完成情况" width="120">
                    <template v-slot="{row}">
                        <span :class="{primary:row.finish===1,success:row.finish===2}" class="dot"/>
                        {{getFinish(row.finish)}}
                    </template>
                </el-table-column>
                <el-table-column align="center" label="完成时间" width="150" show-overflow-tooltip>
                    <template v-slot="{row}">{{row.ftime | timestamp2Date}}</template>
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

        <edit-dialog
                ref="editDialog"
                v-model="editDialog"
                :base-url="baseUrl"
                :id="row?row.id:null"
                :type.sync="type"
                @search="search"
        />
    </el-card>
</template>

<script>
    import EditDialog from './components/EditDialog'
    import SearchForm from '@/bizComponents/SearchForm'
    import SearchFormItem from "@/bizComponents/SearchForm/SearchFormItem"
    import documentTableMixin from '@/mixins/bizDocumentTableMixin'
    import {del, getSubById, search} from "@/api/purchase/order"

    export default {
        name: "purchaseOrder",
        mixins: [documentTableMixin],
        components: {EditDialog, SearchForm, SearchFormItem},
        data() {
            return {
                baseUrl: '/purchase/order',
                searchForm: {
                    sname: null
                },
                temp: {
                    finish: [],
                    ftime: []
                },
                api: {search, del, getSubById},
                excel: {
                    column: [
                        {header: '序号', prop: 'id'},
                        {header: '供应商', prop: 'sname', width: 30},
                        {header: '创建人', prop: 'cname'},
                        {header: '创建时间', prop: 'ctime'},
                        {header: '审核人', prop: 'vname'},
                        {header: '审核时间', prop: 'vtime'},
                        {header: '状态', prop: 'status'},
                        {header: '完成情况', prop: 'finish'},
                        {header: '完成时间', prop: 'ftime'},
                        {header: '备注', prop: 'remark', width: 50}
                    ]
                }
            }
        },
        methods: {
            mergeSearchForm() {
                return {
                    ...this.searchForm,
                    status: this.temp.status.join(','),
                    finish: this.temp.finish.join(','),
                    ctimeStart: this.temp.ctime ? this.temp.ctime[0] : null,
                    ctimeEnd: this.temp.ctime ? this.temp.ctime[1] + 86400000 : null,
                    vtimeStart: this.temp.vtime ? this.temp.vtime[0] : null,
                    vtimeEnd: this.temp.vtime ? this.temp.vtime[1] + 86400000 : null,
                    ftimeStart: this.temp.ftime ? this.temp.ftime[0] : null,
                    ftimeEnd: this.temp.ftime ? this.temp.ftime[1] + 86400000 : null,
                }
            }
        }
    }
</script>

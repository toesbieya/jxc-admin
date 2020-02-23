<template>
    <el-card v-loading="config.operating">
        <search-form>
            <search-form-item :sm="24" :lg="12" :xl="8" label="供应商：">
                <el-input v-model="searchForm.name" clearable maxlength="50"/>
            </search-form-item>
            <search-form-item :sm="24" :lg="12" :xl="8" label="行政区域：">
                <el-input :value="temp.region_name" clearable @clear="clearSidSearch" @focus="regionDialog=true"/>
            </search-form-item>
            <search-form-item :sm="24" :lg="12" :xl="8" label="地 址：">
                <el-input v-model="searchForm.address" clearable maxlength="100"/>
            </search-form-item>
            <search-form-item :sm="24" :lg="12" :xl="8" label="联系人：">
                <el-input v-model="searchForm.linkman" clearable maxlength="50"/>
            </search-form-item>
            <search-form-item :sm="24" :lg="12" :xl="8" label="联系电话：">
                <el-input v-model="searchForm.linkphone" clearable maxlength="20"/>
            </search-form-item>
            <search-form-item :sm="24" :lg="12" :xl="8" label="状 态：">
                <el-select v-model="searchForm.status" clearable @clear="searchForm.status=null">
                    <el-option :value="1" label="启用"/>
                    <el-option :value="0" label="禁用"/>
                </el-select>
            </search-form-item>
            <search-form-item :sm="24" :lg="12" :xl="8" label="创建时间：">
                <el-date-picker
                        v-model="temp.ctime"
                        format="yyyy-MM-dd"
                        range-separator="-"
                        type="daterange"
                        value-format="timestamp"
                />
            </search-form-item>
        </search-form>
        <el-row>
            <el-button size="small" type="success" @click="search">查 询</el-button>
            <el-button v-if="canAdd" size="small" type="primary" @click="add">添 加</el-button>
            <el-button v-if="canUpdate" size="small" type="primary" @click="edit">编 辑</el-button>
            <el-button v-if="canDel" size="small" type="danger" @click="del">删 除</el-button>
        </el-row>
        <el-row v-loading="config.loading" class="table-container">
            <el-table
                    ref="table"
                    :data="tableData"
                    current-row-key="id"
                    highlight-current-row
                    row-key="id"
                    @row-click="row=$event"
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
        <edit-dialog v-model="editDialog" :data="row" :type="type" @success="success"/>
        <region-selector v-model="regionDialog" limit :limit-api="getLimitRegion" @select="selectRegion"/>
    </el-card>
</template>

<script>
    import SearchForm from "@/bizComponents/SearchForm"
    import SearchFormItem from "@/bizComponents/SearchForm/SearchFormItem"
    import RegionSelector from "@/bizComponents/RegionSelector"
    import EditDialog from './EditDialog'
    import {delSupplier, getLimitRegion, getSuppliers} from "@/api/system/supplier"
    import {isEmpty} from '@/utils'
    import {elConfirm, elError, elSuccess} from "@/utils/message"
    import {auth} from "@/utils/auth"
    import tableMixin from '@/mixins/tablePageMixin'

    const baseUrl = '/system/supplier'

    export default {
        name: "supplierManagement",
        mixins: [tableMixin],
        components: {SearchForm, SearchFormItem, EditDialog, RegionSelector},
        data() {
            return {
                searchForm: {
                    name: '',
                    address: '',
                    linkman: '',
                    linkphone: '',
                    region: null,
                    status: null
                },
                temp: {
                    region_name: null,
                    ctime: []
                },
                editDialog: false,
                regionDialog: false
            }
        },
        computed: {
            canAdd() {
                return auth(baseUrl + '/add')
            },
            canUpdate() {
                return auth(baseUrl + '/update')
            },
            canDel() {
                return auth(baseUrl + '/del')
            }
        },
        methods: {
            getLimitRegion() {
                return getLimitRegion()
            },
            clearSidSearch() {
                this.searchForm.region = null
                this.temp.region_name = null
            },
            selectRegion(obj, ids) {
                this.searchForm.region = ids.join(',')
                this.temp.region_name = obj.fullname
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
                this.row = null
                this.type = 'see'
                getSuppliers(this.mergeSearchForm())
                    .then(({list, total}) => {
                        this.searchForm.total = total
                        this.tableData = list
                    })
                    .finally(() => this.config.loading = false)
            },
            add() {
                this.type = 'add'
                this.editDialog = true
            },
            edit() {
                if (isEmpty(this.row)) return elError('请选择要编辑的供应商')
                this.type = 'edit'
                this.editDialog = true
            },
            del() {
                if (isEmpty(this.row)) return elError('请选择要删除的供应商')
                if (this.config.operating) return
                elConfirm(`确定删除供应商【${this.row.name}】？`)
                    .then(() => {
                        this.config.operating = true
                        return delSupplier({id: this.row.id, name: this.row.name})
                    })
                    .then(() => this.success('删除成功'))
                    .finally(() => this.config.operating = false)
            },
            success(msg) {
                elSuccess(msg)
                this.editDialog = false
                this.search()
                this.$refs.tree.init()
            }
        }
    }
</script>

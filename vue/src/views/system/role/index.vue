<template>
    <el-card v-loading="config.operating">
        <search-form>
            <search-form-item label="角色名：">
                <el-input v-model="searchForm.name" clearable maxlength="100"/>
            </search-form-item>
            <search-form-item label="创建人：">
                <el-input v-model="searchForm.cname" clearable maxlength="100"/>
            </search-form-item>
            <search-form-item label="状 态：">
                <el-select v-model="searchForm.status" clearable @clear="searchForm.status=null">
                    <el-option :value="1" label="启用"/>
                    <el-option :value="0" label="禁用"/>
                </el-select>
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
        </search-form>
        <el-row>
            <el-button icon="el-icon-search" size="small" type="success" @click="search">查 询</el-button>
            <el-button v-if="canAdd" icon="el-icon-plus" size="small" type="primary" @click="add">添 加</el-button>
            <el-button v-if="canUpdate" icon="el-icon-edit" size="small" type="primary" @click="edit">编 辑</el-button>
            <el-button v-if="canDel" icon="el-icon-delete" size="small" type="danger" @click="del">删 除</el-button>
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
                <el-table-column align="center" type="expand">
                    <role-resource slot-scope="{row}" :ids="row.resource_id" :map="resourceMap"/>
                </el-table-column>
                <el-table-column align="center" label="#" type="index" width="80"/>
                <el-table-column align="center" label="角色名" prop="name" show-overflow-tooltip/>
                <el-table-column align="center" label="创建人" prop="cname" show-overflow-tooltip/>
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
    </el-card>
</template>

<script>
    import SearchForm from "@/bizComponents/SearchForm"
    import SearchFormItem from "@/bizComponents/SearchForm/SearchFormItem"
    import EditDialog from './components/EditDialog'
    import RoleResource from "./components/RoleResource"
    import {delRole, searchRoles} from "@/api/system/role"
    import {isEmpty} from '@/utils'
    import {elConfirm, elError, elSuccess} from "@/utils/message"
    import {auth} from "@/utils/auth"
    import tableMixin from '@/mixins/tablePageMixin'

    const baseUrl = '/system/role'

    export default {
        name: "roleManagement",
        mixins: [tableMixin],
        components: {SearchForm, SearchFormItem, EditDialog, RoleResource},
        data() {
            return {
                searchForm: {
                    name: '',
                    cname: '',
                    status: null
                },
                temp: {
                    ctime: []
                },
                editDialog: false
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
            },
            resourceMap() {
                const resource = this.$store.state.resource
                if (resource.data.length <= 0) return {}
                return resource.data.reduce((map, resource) => {
                    map[resource.id] = resource
                    return map
                }, {})
            }
        },
        methods: {
            mergeSearchForm() {
                return {
                    ...this.searchForm,
                    startTime: this.temp.ctime ? this.temp.ctime[0] : null,
                    endTime: this.temp.ctime ? this.temp.ctime[1] + 86400000 : null,
                }
            },
            search() {
                if (this.config.loading) return
                this.config.loading = true
                this.row = null
                this.type = 'see'
                searchRoles(this.mergeSearchForm())
                    .then(({list, total}) => {
                        list.forEach(i => {
                            i.resource_id = isEmpty(i.resource_id) ? [] : i.resource_id.split(',').map(i => parseInt(i))
                        })
                        this.searchForm.total = total
                        this.tableData = list
                    })
                    .finally(() => this.config.loading = false)
            },
            add() {
                this.row = null
                this.type = 'add'
                this.editDialog = true
            },
            edit() {
                if (!this.row) return elError('请选择要编辑的角色')
                this.type = 'edit'
                this.editDialog = true
            },
            del() {
                if (!this.row) return elError('请选择要删除的角色')
                if (this.row.status === 1) return elError('不能删除已启用的角色')
                if (this.config.operating) return
                elConfirm(`确定删除角色【${this.row.name}】？`)
                    .then(() => {
                        this.config.operating = true
                        return delRole({id: this.row.id, name: this.row.name})
                    })
                    .then(() => {
                        elSuccess('删除成功')
                        this.search()
                    })
                    .finally(() => this.config.operating = false)
            },
            success(msg) {
                elSuccess(msg)
                this.editDialog = false
                this.search()
            }
        }
    }
</script>

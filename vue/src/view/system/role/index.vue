<template>
    <el-card v-loading="config.operating">
        <search-form>
            <search-form-item label="角色名">
                <el-input v-model="searchForm.name" clearable maxlength="100"/>
            </search-form-item>
            <search-form-item label="创建人">
                <el-input v-model="searchForm.cname" clearable maxlength="100"/>
            </search-form-item>
            <search-form-item label="状 态">
                <el-select v-model="searchForm.status" clearable @clear="searchForm.status=null">
                    <el-option :value="1" label="启用"/>
                    <el-option :value="0" label="禁用"/>
                </el-select>
            </search-form-item>
            <search-form-item label="创建时间">
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
            <el-button v-if="canAdd" icon="el-icon-plus" size="small" type="primary" @click="add">添 加</el-button>
            <el-button v-if="canUpdate" icon="el-icon-edit" size="small" type="primary" @click="edit">编 辑</el-button>
            <el-button v-if="canDel" icon="el-icon-delete" size="small" type="danger" @click="del">删 除</el-button>
        </el-row>

        <el-row v-loading="config.loading" class="table-container">
            <abstract-table
                :data="tableData"
                @row-click="rowClick"
            >
                <el-table-column align="center" label="#" type="index" width="80"/>
                <el-table-column align="center" label="角色名" prop="name" show-overflow-tooltip/>
                <el-table-column align="center" label="创建人" prop="cname" show-overflow-tooltip/>
                <el-table-column align="center" label="创建时间" width="150" show-overflow-tooltip>
                    <template v-slot="{row}">{{ row.ctime | timestamp2Date }}</template>
                </el-table-column>
                <el-table-column align="center" label="状 态" width="120">
                    <template v-slot="{row}">
                        <span :class="row.status===1?'success':'error'" class="dot"/>
                        <span>{{ row.status === 1 ? '启用' : '禁用' }}</span>
                    </template>
                </el-table-column>
            </abstract-table>

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
import tableMixin from '@/mixin/tablePageMixin'
import EditDialog from './EditDialog'
import SearchForm from "@/component/SearchForm"
import SearchFormItem from "@/component/SearchForm/SearchFormItem"
import {baseUrl, delRole, searchRoles} from "@/api/system/role"
import {isEmpty} from '@/util'
import {auth} from "@/util/auth"
import {elConfirm, elError, elSuccess} from "@/util/message"

export default {
    name: "roleManagement",

    mixins: [tableMixin],

    components: {EditDialog, SearchForm, SearchFormItem},

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
            return auth(`${baseUrl}/add`)
        },
        canUpdate() {
            return auth(`${baseUrl}/update`)
        },
        canDel() {
            return auth(`${baseUrl}/del`)
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

        str2intArray(str) {
            if (isEmpty(str)) return []
            return str.split(',').map(i => parseInt(i))
        },

        search() {
            if (this.config.loading) return
            this.config.loading = true
            this.row = null
            this.type = 'see'
            searchRoles(this.mergeSearchForm())
                .then(({list, total}) => {
                    list.forEach(i => {
                        i.departmentId = this.str2intArray(i.departmentId)
                        i.resourceId = this.str2intArray(i.resourceId)
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
            const {id, name, status} = this.row
            if (status === 1) return elError('不能删除已启用的角色')
            if (this.config.operating) return
            elConfirm(`确定删除角色【${name}】？`)
                .then(() => {
                    this.config.operating = true
                    return delRole({id, name})
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

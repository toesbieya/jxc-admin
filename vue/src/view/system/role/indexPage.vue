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
                <el-select v-model="searchForm.enable" clearable @clear="searchForm.enable = null">
                    <el-option :value="true" label="启用"/>
                    <el-option :value="false" label="禁用"/>
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
            <el-button icon="el-icon-search" size="small" type="primary" @click="search">查 询</el-button>
            <el-button v-if="canAdd" icon="el-icon-plus" size="small" type="primary" @click="add">添 加</el-button>
            <el-button v-if="canUpdate" icon="el-icon-edit" size="small" type="primary" @click="edit">编 辑</el-button>
            <el-button v-if="canDel" icon="el-icon-delete" size="small" type="danger" @click="del">删 除</el-button>
        </el-row>

        <el-row v-loading="config.loading" class="table-container">
            <abstract-table :data="tableData" @row-click="rowClick">
                <el-table-column align="center" label="#" type="index" width="80"/>
                <el-table-column align="center" label="角色名" prop="name" show-overflow-tooltip/>
                <el-table-column align="center" label="创建人" prop="cname" show-overflow-tooltip/>
                <el-table-column align="center" label="创建时间" width="150">
                    <template v-slot="{row}">{{ row.ctime | timestamp2Date }}</template>
                </el-table-column>
                <el-table-column align="center" label="状 态" width="120">
                    <template v-slot="{row}">
                        <span :class="row.enable ? 'success' : 'error'" class="dot"/>
                        <span>{{ row.enable ? '启用' : '禁用' }}</span>
                    </template>
                </el-table-column>
            </abstract-table>

            <abstract-pagination :model="searchForm" @current-change="pageChange"/>
        </el-row>

        <edit-dialog v-model="editDialog" :data="row" :type="type" @success="success"/>
    </el-card>
</template>

<script>
import tableMixin from '@/mixin/tablePageMixin'
import EditDialog from './EditDialog'
import SearchForm from "@/component/form/Search"
import SearchFormItem from "@/component/form/Search/item"
import {add, update, del, search} from "@/api/system/role"
import {isEmpty} from '@/util'
import {wic} from "@/util/auth"
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
                enable: null
            },
            temp: {
                ctime: []
            },
            editDialog: false
        }
    },

    computed: wic({add, update, del}),

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
            search
                .request(this.mergeSearchForm())
                .then(({data: {list, total}}) => {
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
            const {id, name, enable} = this.row
            if (enable) return elError('不能删除已启用的角色')
            if (this.config.operating) return
            elConfirm(`确定删除角色【${name}】？`)
                .then(() => {
                    this.config.operating = true
                    return del.request({id, name})
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

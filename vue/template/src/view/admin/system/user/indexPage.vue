<template>
    <list-page :data="listPageConfig">
        <template v-slot:searchForm>
            <el-form-item label="用户名">
                <el-input v-model="searchForm.loginName" clearable maxlength="20"/>
            </el-form-item>
            <el-form-item label="昵 称">
                <el-input v-model="searchForm.nickName" clearable maxlength="100"/>
            </el-form-item>
            <el-form-item label="角 色">
                <role-selector v-model="searchForm.role"/>
            </el-form-item>
            <el-form-item label="状 态">
                <el-select v-model="searchForm.enable" clearable @clear="searchForm.enable = null">
                    <el-option :value="true" label="启用"/>
                    <el-option :value="false" label="禁用"/>
                </el-select>
            </el-form-item>
            <el-form-item label="创建时间">
                <el-date-picker
                    v-model="temp.ctime"
                    format="yyyy-MM-dd"
                    range-separator="-"
                    type="daterange"
                    value-format="timestamp"
                />
            </el-form-item>
        </template>

        <template v-slot:tableColumn>
            <el-table-column align="center" label="#" type="index" width="80"/>
            <el-table-column align="center" label="头像" width="100">
                <img
                    slot-scope="{row}"
                    v-if="row.avatar"
                    :src="row.avatar"
                    class="table-image"
                    @click.stop="() => previewAvatar(row.avatar)"
                >
            </el-table-column>
            <el-table-column align="center" label="登录名" prop="loginName" show-overflow-tooltip/>
            <el-table-column align="center" label="昵称" prop="nickName" show-overflow-tooltip/>
            <el-table-column align="center" label="角 色" show-overflow-tooltip>
                <template v-slot="{row}">
                    {{ row.admin === true ? '超级管理员' : row.roleName }}
                </template>
            </el-table-column>
            <el-table-column align="center" label="部 门" prop="deptName" show-overflow-tooltip/>
            <el-table-column align="center" label="创建时间" width="150">
                <template v-slot="{row}">{{ row.ctime | timestamp2Date }}</template>
            </el-table-column>
            <el-table-column align="center" label="在线情况" width="120">
                <template v-slot="{row}">
                    <span :class="row.online ? 'success' : 'error'" class="dot"/>
                    <span>{{ row.online ? '在线' : '离线' }}</span>
                </template>
            </el-table-column>
            <el-table-column align="center" label="状 态" width="120">
                <template v-slot="{row}">
                    <span :class="row.enable ? 'success' : 'error'" class="dot"/>
                    <span>{{ row.enable ? '启用' : '禁用' }}</span>
                </template>
            </el-table-column>
        </template>

        <edit-dialog v-model="editDialog" :data="row" :type="type" @success="success"/>
    </list-page>
</template>

<script>
import tableMixin from '@/mixin/tablePageMixin'
import ListPage from '@/view/_common/ListPage'
import EditDialog from './component/EditDialog'
import RoleSelector from './component/RoleSelector'
import {search, add, update, del, kick, resetPwd} from "@/api/system/user"
import {isEmpty} from '@/util'
import {wic} from "@/util/auth"
import {autoCompleteUrl} from "@/util/file"
import {elConfirm, elError, elSuccess} from "@/util/message"

export default {
    name: "userManagement",

    mixins: [tableMixin],

    components: {ListPage, EditDialog, RoleSelector},

    data() {
        return {
            searchForm: {
                loginName: '',
                nickName: '',
                role: null,
                enable: null
            },
            temp: {ctime: []},
            editDialog: false
        }
    },

    computed: {
        ...wic({add, update, del, kick, resetPwd}),

        listPageConfig() {
            return {
                pageLoading: this.config.operating,
                buttons: [
                    this.canAdd && {icon: 'el-icon-plus', e: this.add, content: '添 加'},
                    {icon: 'el-icon-view', e: this.see, content: '查 看'},
                    this.canUpdate && {icon: 'el-icon-edit', e: this.edit, content: '编 辑'},
                    this.canDel && {icon: 'el-icon-delete', e: this.del, content: '删 除'},
                    this.canResetPwd && {icon: 'el-icon-delete', e: this.resetPwd, content: '重置密码'},
                    this.canKick && {icon: 'el-icon-delete', e: this.kick, content: '踢 出'}
                ],
                dataLoading: this.config.loading,
                search: {
                    props: {model: this.searchForm},
                    on: {search: this.search}
                },
                table: {
                    props: {data: this.tableData},
                    on: {'row-click': this.rowClick}
                },
                pagination: {
                    props: {model: this.searchForm},
                    on: {'current-change': this.pageChange}
                }
            }
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
            search
                .request(this.mergeSearchForm())
                .then(({data: {list, total}}) => {
                    list.forEach(u => u.avatar = autoCompleteUrl(u.avatar))
                    this.searchForm.total = total
                    this.tableData = list
                })
                .finally(() => this.config.loading = false)
        },

        add() {
            this.type = 'add'
            this.editDialog = true
        },

        see() {
            if (isEmpty(this.row)) return elError('请选择要查看的用户')
            this.type = 'see'
            this.editDialog = true
        },

        edit() {
            if (isEmpty(this.row)) return elError('请选择要编辑的用户')
            this.type = 'edit'
            this.editDialog = true
        },

        resetPwd() {
            if (isEmpty(this.row)) return elError('请选择要重置密码的用户')
            if (this.config.operating) return
            elConfirm(`确定重置用户【${this.row.loginName}】的密码？`)
                .then(() => {
                    this.config.operating = true
                    return resetPwd.request({id: this.row.id, loginName: this.row.loginName})
                })
                .then(({msg}) => {
                    elSuccess(msg)
                    this.search()
                })
                .finally(() => this.config.operating = false)
        },

        del() {
            if (isEmpty(this.row)) return elError('请选择要删除的用户')
            if (this.config.operating) return
            elConfirm(`确定删除用户【${this.row.loginName}】？`)
                .then(() => {
                    this.config.operating = true
                    return del.request({id: this.row.id, LoginName: this.row.loginName})
                })
                .then(({msg}) => {
                    elSuccess(msg)
                    this.search()
                })
                .finally(() => this.config.operating = false)
        },

        kick() {
            if (isEmpty(this.row)) return elError('请选择要踢出的用户')
            if (!this.row.online) return elError('只有在线用户才可以踢出')
            if (this.config.operating) return
            elConfirm(`确定踢出用户【${this.row.loginName}】？`)
                .then(() => {
                    this.config.operating = true
                    return kick.request([{id: this.row.id, LoginName: this.row.loginName}])
                })
                .then(() => {
                    elSuccess('踢出成功')
                    this.search()
                })
                .finally(() => this.config.operating = false)
        },

        success(msg) {
            elSuccess(msg)
            this.editDialog = false
            this.search()
        },

        previewAvatar(src) {
            this.$image({index: 0, urlList: [src]})
        }
    }
}
</script>

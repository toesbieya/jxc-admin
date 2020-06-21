<template>
    <el-card v-loading="config.operating">
        <search-form>
            <search-form-item label="用户名：">
                <el-input v-model="searchForm.name" clearable maxlength="100"/>
            </search-form-item>
            <search-form-item label="角 色：">
                <role-selector v-model="searchForm.role"/>
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

        <el-row class="button-group">
            <el-button icon="el-icon-search" size="small" type="success" @click="search">查 询</el-button>
            <el-button v-if="canAdd" icon="el-icon-plus" size="small" type="primary" @click="add">添 加</el-button>
            <el-button v-if="canUpdate" icon="el-icon-edit" size="small" type="primary" @click="edit">编 辑</el-button>
            <el-button v-if="canResetPwd" icon="el-icon-edit" size="small" type="dashed" plain @click="resetPwd">重置密码
            </el-button>
            <el-button v-if="canDel" icon="el-icon-delete" size="small" type="danger" @click="del">删 除</el-button>
            <el-button v-if="canKick" icon="el-icon-warning-outline" size="small" type="danger" @click="kick">踢 出
            </el-button>
        </el-row>

        <el-row v-loading="config.loading" class="table-container">
            <abstract-table :data="tableData" @row-click="row=$event">
                <el-table-column align="center" label="#" type="index" width="80"/>
                <el-table-column align="center" label="头像" width="100">
                    <img
                            slot-scope="{row}"
                            v-if="row.avatar"
                            :src="row.avatar"
                            class="table-image"
                            @click.stop="previewAvatar(row.avatar)"
                    >
                </el-table-column>
                <el-table-column align="center" label="用户名" prop="name" show-overflow-tooltip/>
                <el-table-column align="center" label="角 色" prop="role_name" show-overflow-tooltip/>
                <el-table-column align="center" label="创建时间" width="150" show-overflow-tooltip>
                    <template v-slot="{row}">{{row.ctime | timestamp2Date}}</template>
                </el-table-column>
                <el-table-column align="center" label="在线情况" width="120">
                    <template v-slot="{row}">
                        <span :class="row.online?'success':'error'" class="dot"/>
                        <span>{{row.online?'在线':'离线'}}</span>
                    </template>
                </el-table-column>
                <el-table-column align="center" label="状 态" width="120">
                    <template v-slot="{row}">
                        <span :class="row.status===1?'success':'error'" class="dot"/>
                        <span>{{row.status===1?'启用':'禁用'}}</span>
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
    import SearchForm from "@/components/SearchForm"
    import SearchFormItem from "@/components/SearchForm/SearchFormItem"
    import RoleSelector from './components/RoleSelector'
    import EditDialog from './components/EditDialog'
    import {delUser, getUsers, kick, resetUserPwd} from "@/api/system/user"
    import {isEmpty} from '@/utils'
    import {autoCompleteUrl} from "@/utils/file"
    import {elConfirm, elError, elSuccess} from "@/utils/message"
    import {auth} from "@/utils/auth"
    import tableMixin from '@/mixins/tablePageMixin'

    const baseUrl = '/system/user'

    export default {
        name: "userManagement",

        mixins: [tableMixin],

        components: {SearchForm, SearchFormItem, RoleSelector, EditDialog},

        data() {
            return {
                searchForm: {
                    name: '',
                    role: null,
                    status: null
                },
                temp: {ctime: []},
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

            canKick() {
                return auth(baseUrl + '/kick')
            },

            canResetPwd() {
                return auth(baseUrl + '/resetPwd')
            },
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
                getUsers(this.mergeSearchForm())
                    .then(({list, total}) => {
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

            edit() {
                if (isEmpty(this.row)) return elError('请选择要编辑的用户')
                this.type = 'edit'
                this.editDialog = true
            },

            resetPwd() {
                if (isEmpty(this.row)) return elError('请选择要重置密码的用户')
                if (this.config.operating) return
                elConfirm(`确定重置用户【${this.row.name}】的密码？`)
                    .then(() => {
                        this.config.operating = true
                        return resetUserPwd({id: this.row.id, name: this.row.name})
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
                elConfirm(`确定删除用户【${this.row.name}】？`)
                    .then(() => {
                        this.config.operating = true
                        return delUser({id: this.row.id, name: this.row.name})
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
                elConfirm(`确定踢出用户【${this.row.name}】？`)
                    .then(() => {
                        this.config.operating = true
                        return kick([{id: this.row.id, name: this.row.name}])
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

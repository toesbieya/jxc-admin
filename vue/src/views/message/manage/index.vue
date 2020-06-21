<template>
    <el-card v-loading="config.operating">
        <search-form>
            <search-form-item label="标 题：">
                <el-input v-model="searchForm.title" clearable maxlength="100"/>
            </search-form-item>
            <search-form-item label="消息类型：">
                <el-select v-model="searchForm.type" clearable @clear="searchForm.type=null">
                    <el-option :value="0" label="通知提醒"/>
                    <el-option :value="1" label="系统公告"/>
                </el-select>
            </search-form-item>
            <search-form-item label="状 态：">
                <el-select v-model="searchForm.status" clearable @clear="searchForm.status=null">
                    <el-option :value="0" label="拟定"/>
                    <el-option :value="1" label="已发布"/>
                    <el-option :value="2" label="已撤回"/>
                </el-select>
            </search-form-item>
        </search-form>

        <el-row class="button-group">
            <el-button icon="el-icon-search" size="small" type="success" @click="search">查 询</el-button>
            <el-button v-if="canAdd" icon="el-icon-plus" size="small" type="primary" @click="add">添 加</el-button>
            <el-button icon="el-icon-view" size="small" type="primary" @click="see">查 看</el-button>
            <el-button v-if="canUpdate" icon="el-icon-edit" size="small" type="primary" @click="edit">编 辑</el-button>
            <el-button v-if="canDel" icon="el-icon-delete" size="small" type="danger" @click="del">删 除</el-button>
        </el-row>

        <el-row v-loading="config.loading" class="table-container">
            <abstract-table :data="tableData" @row-click="row=$event">
                <el-table-column align="center" label="#" type="index" width="80"/>
                <el-table-column align="center" label="标 题" prop="title" show-overflow-tooltip/>
                <el-table-column align="center" label="类 型" show-overflow-tooltip>
                    <template v-slot="{row}">{{row.type===0?'通知提醒':'系统公告'}}</template>
                </el-table-column>
                <el-table-column align="center" label="通知对象" show-overflow-tooltip>
                    <template v-slot="{row}">{{row.all===1?'全体用户':'指定用户'}}</template>
                </el-table-column>
                <el-table-column align="center" label="状 态" show-overflow-tooltip>
                    <template v-slot="{row}">{{transformStatus(row.status)}}</template>
                </el-table-column>
                <el-table-column align="center" label="发布人" prop="pname" show-overflow-tooltip/>
                <el-table-column align="center" label="发布时间" width="150" show-overflow-tooltip>
                    <template v-slot="{row}">{{row.ptime | timestamp2Date}}</template>
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

        <edit-dialog
                v-model="editDialog"
                :base-url="baseUrl"
                :data="row"
                :type.sync="type"
                @search="search"
        />
    </el-card>
</template>

<script>
    import SearchForm from "@/components/SearchForm"
    import SearchFormItem from "@/components/SearchForm/SearchFormItem"
    import EditDialog from './EditDialog'
    import {search, del} from "@/api/message/manage"
    import {isEmpty} from '@/utils'
    import {elConfirm, elError, elSuccess} from "@/utils/message"
    import {auth} from "@/utils/auth"
    import tableMixin from '@/mixins/tablePageMixin'

    export default {
        name: "messageManagement",

        mixins: [tableMixin],

        components: {SearchForm, SearchFormItem, EditDialog},

        data() {
            return {
                baseUrl: '/message/manage',
                searchForm: {
                    title: null,
                    type: null,
                    status: null
                },
                type: 'add',
                editDialog: false,
                regionDialog: false
            }
        },

        computed: {
            canAdd() {
                return auth(this.baseUrl + '/add')
            },

            canUpdate() {
                return auth(this.baseUrl + '/update')
            },

            canDel() {
                return auth(this.baseUrl + '/del')
            }
        },

        methods: {
            transformStatus(status) {
                switch (status) {
                    case 0:
                        return '拟定'
                    case 1:
                        return '已发布'
                    case 2:
                        return '已撤回'
                }
            },

            search() {
                if (this.config.loading) return
                this.config.loading = true
                this.row = null
                this.type = 'see'
                search(this.searchForm)
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

            see() {
                if (isEmpty(this.row)) return elError('请选择要查看的消息')
                this.type = 'see'
                this.editDialog = true
            },

            edit() {
                if (isEmpty(this.row)) return elError('请选择要编辑的消息')
                this.type = 'edit'
                this.editDialog = true
            },

            del() {
                if (isEmpty(this.row)) return elError('请选择要删除的消息')
                if (this.row.status !== 0) return elError('只有状态为【拟定】时才能删除')
                if (this.config.operating) return
                elConfirm(`确定删除消息【${this.row.title}】？`)
                    .then(() => {
                        this.config.operating = true
                        return del(this.row.id)
                    })
                    .then(() => {
                        elSuccess('删除成功')
                        this.search()
                    })
                    .finally(() => this.config.operating = false)
            }
        }
    }
</script>

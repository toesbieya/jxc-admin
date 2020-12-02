<template>
    <list-page :data="listPageConfig">
        <template v-slot:searchForm>
            <el-form-item label="标 题">
                <el-input v-model="searchForm.title" clearable maxlength="100"/>
            </el-form-item>
            <el-form-item label="消息类型">
                <el-select v-model="searchForm.type" clearable @clear="searchForm.type=null">
                    <el-option :value="0" label="通知提醒"/>
                    <el-option :value="1" label="系统公告"/>
                </el-select>
            </el-form-item>
            <el-form-item label="状 态">
                <el-select v-model="searchForm.status" clearable @clear="searchForm.status=null">
                    <el-option :value="0" label="拟定"/>
                    <el-option :value="1" label="已发布"/>
                    <el-option :value="2" label="已撤回"/>
                </el-select>
            </el-form-item>
        </template>

        <template v-slot:tableColumn>
            <el-table-column align="center" label="#" type="index" width="80"/>
            <el-table-column align="center" label="标 题" prop="title" show-overflow-tooltip/>
            <el-table-column align="center" label="类 型" show-overflow-tooltip>
                <template v-slot="{row}">{{ row.type === 0 ? '通知提醒' : '系统公告' }}</template>
            </el-table-column>
            <el-table-column align="center" label="通知对象" show-overflow-tooltip>
                <template v-slot="{row}">{{ row.broadcast ? '全体用户' : '指定用户' }}</template>
            </el-table-column>
            <el-table-column align="center" label="状 态" show-overflow-tooltip>
                <template v-slot="{row}">{{ transformStatus(row.status) }}</template>
            </el-table-column>
            <el-table-column align="center" label="发布人" prop="pname" show-overflow-tooltip/>
            <el-table-column align="center" label="发布时间" width="150">
                <template v-slot="{row}">{{ row.ptime | timestamp2Date }}</template>
            </el-table-column>
        </template>

        <edit-dialog v-model="editDialog" :data="row" :type.sync="type" @search="search"/>
    </list-page>
</template>

<script>
import tableMixin from '@/mixin/tablePageMixin'
import EditDialog from './EditDialog'
import ListPage from '@/view/_common/ListPage'
import {search, add, update, del} from "@/api/message/manage"
import {isEmpty} from '@/util'
import {wic} from "@/util/auth"
import {elConfirm, elError, elSuccess} from "@/util/message"

export default {
    name: "messageManagement",

    mixins: [tableMixin],

    components: {EditDialog, ListPage},

    data() {
        return {
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
        ...wic({add, update, del}),

        listPageConfig() {
            return {
                pageLoading: this.config.operating,
                buttons: [
                    this.canAdd && {icon: 'el-icon-plus', e: this.add, content: '添 加'},
                    {icon: 'el-icon-view', e: this.see, content: '查 看'},
                    this.canUpdate && {icon: 'el-icon-edit', e: this.edit, content: '编 辑'},
                    this.canDel && {icon: 'el-icon-delete', e: this.del, content: '删 除'}
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
            search
                .request(this.searchForm)
                .then(({data: {list, total}}) => {
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
                    return del.request(this.row.id, this.row.title)
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

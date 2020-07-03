<template>
    <el-card v-loading="config.operating">
        <el-row>
            <el-button icon="el-icon-search" size="small" type="success" @click="search">查 询</el-button>
            <el-button v-if="canUpdate" icon="el-icon-edit" size="small" type="primary" @click="edit">编 辑</el-button>
        </el-row>

        <el-row v-loading="config.loading" class="table-container">
            <abstract-table :data="tableData" @row-click="rowClick">
                <el-table-column label="名称" prop="fullName" show-overflow-tooltip/>
                <el-table-column label="访问路径" prop="url" show-overflow-tooltip/>
                <el-table-column label="总频率" prop="total_rate" show-overflow-tooltip/>
                <el-table-column label="单个IP频率" prop="ip_rate" show-overflow-tooltip/>
            </abstract-table>
        </el-row>

        <edit-dialog v-model="editDialog" :data="row" :type="type" @success="success"/>
    </el-card>
</template>

<script>
    import SearchForm from "@/components/SearchForm"
    import SearchFormItem from "@/components/SearchForm/SearchFormItem"
    import EditDialog from './EditDialog'
    import {getAllResources} from "@/api/system/resource"
    import {elError, elSuccess} from "@/utils/message"
    import {auth} from "@/utils/auth"
    import {createTreeByWorker} from "@/utils/tree"
    import tableMixin from '@/mixins/tablePageMixin'

    const baseUrl = '/system/resource'

    export default {
        name: "resourceManagement",

        mixins: [tableMixin],

        components: {SearchForm, SearchFormItem, EditDialog},

        data() {
            return {
                editDialog: false
            }
        },

        computed: {
            canUpdate() {
                return auth(baseUrl + '/update')
            }
        },

        methods: {
            search() {
                if (this.config.loading) return
                this.config.loading = true
                this.row = null
                getAllResources()
                    .then(data => createTreeByWorker(data))
                    .then(data => this.tableData = data)
                    .finally(() => this.config.loading = false)
            },

            edit() {
                if (!this.row) return elError('请选择要编辑的资源')
                this.editDialog = true
            },

            success(msg) {
                elSuccess(msg)
                this.editDialog = false
                this.search()
            }
        }
    }
</script>

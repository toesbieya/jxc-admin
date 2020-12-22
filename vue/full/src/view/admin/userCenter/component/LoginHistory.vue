<template>
    <div v-loading="config.loading">
        <abstract-table :data="tableData" :highlight-current-row="false">
            <el-table-column align="center" type="index" width="60">
                <template v-slot:header>
                    <el-button icon="el-icon-refresh-right" style="padding: 0" type="text" @click="search"/>
                </template>
            </el-table-column>
            <el-table-column align="center" label="ip" prop="ip" show-overflow-tooltip/>
            <el-table-column align="center" label="地址" prop="address" show-overflow-tooltip/>
            <el-table-column align="center" label="时间" show-overflow-tooltip>
                <template v-slot="{row}">{{ row.time | timestamp2Date }}</template>
            </el-table-column>
            <el-table-column align="center" label="说明" width="80">
                <template v-slot="{row}">
                    <el-tag :type="row.login ? 'success' : 'danger'" size="small" effect="dark">
                        {{ row.login ? '登录' : '登出' }}
                    </el-tag>
                </template>
            </el-table-column>
        </abstract-table>

        <abstract-pagination :model="searchForm" @current-change="pageChange"/>
    </div>
</template>

<script>
import tablePageMixin from '@/mixin/tablePageMixin'
import {searchLoginHistory} from "@/api/record"

export default {
    name: "LoginHistory",

    mixins: [tablePageMixin],

    data() {
        return {
            searchForm: {
                pageSize: 10
            },
        }
    },

    computed: {
        uid() {
            return this.$store.state.user.id
        }
    },

    methods: {
        search() {
            if (this.config.loading) return
            this.config.loading = true
            searchLoginHistory
                .request({...this.searchForm, uid: this.uid})
                .then(({data: {list, total}}) => {
                    this.searchForm.total = total
                    this.tableData = list
                })
                .finally(() => this.config.loading = false)
        }
    }
}
</script>

<template>
    <div v-loading="config.loading">
        <abstract-table :data="tableData" :highlight-current-row="false">
            <el-table-column align="center" type="index" width="60">
                <el-button icon="el-icon-refresh-right" slot="header" style="padding: 0" type="text" @click="search"/>
            </el-table-column>
            <el-table-column align="center" label="ip" prop="ip" show-overflow-tooltip/>
            <el-table-column align="center" label="地址" prop="address" show-overflow-tooltip/>
            <el-table-column align="center" label="时间" show-overflow-tooltip>
                <template v-slot="{row}">{{ row.time | timestamp2Date }}</template>
            </el-table-column>
            <el-table-column align="center" label="说明" width="80">
                <template v-slot="{row}">
                    <el-tag :type="row.type===1?'success':'danger'" size="small" effect="dark">
                        {{ getInfo(row.type) }}
                    </el-tag>
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
            searchLoginHistory({...this.searchForm, uid: this.uid})
                .then(({list, total}) => {
                    this.searchForm.total = total
                    this.tableData = list
                })
                .finally(() => this.config.loading = false)
        },

        getInfo(type) {
            switch (type) {
                case 0:
                    return '登出'
                case 1:
                    return '登陆'
            }
            return null
        }
    }
}
</script>

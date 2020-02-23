<template>
    <div v-loading="loading">
        <el-table :data="tableData">
            <el-table-column align="center" type="index" width="60">
                <el-button icon="el-icon-refresh-right" slot="header" style="padding: 0" type="text" @click="search"/>
            </el-table-column>
            <el-table-column align="center" label="ip" prop="ip" show-overflow-tooltip/>
            <el-table-column align="center" label="地址" prop="address" show-overflow-tooltip/>
            <el-table-column align="center" label="时间" show-overflow-tooltip>
                <template v-slot="{row}">{{row.time | timestamp2Date}}</template>
            </el-table-column>
            <el-table-column align="center" label="说明" width="80">
                <template v-slot="{row}">
                    <el-tag :type="row.type===1?'success':'danger'" effect="dark">
                        {{getInfo(row.type)}}
                    </el-tag>
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
    </div>
</template>

<script>
    import {getLoginHistory} from "@/api/system/user"

    export default {
        name: "LoginHistory",
        data() {
            return {
                searchForm: {
                    page: 1,
                    pageSize: 10,
                    total: 0
                },
                loading: false,
                tableData: [],
            }
        },
        computed: {
            uid() {
                return this.$store.state.user.id
            }
        },
        methods: {
            pageChange(v) {
                this.searchForm.page = v
                this.search()
            },
            search() {
                if (this.loading) return
                this.loading = true
                getLoginHistory({...this.searchForm, uid: this.uid})
                    .then(({list, total}) => {
                        this.searchForm.total = total
                        this.tableData = list
                    })
                    .finally(() => this.loading = false)
            },
            getInfo(type) {
                switch (type) {
                    case 0:
                        return '登录'
                    case 1:
                        return '登出'
                }
                return null
            }
        },
        mounted() {
            this.search()
        }
    }
</script>

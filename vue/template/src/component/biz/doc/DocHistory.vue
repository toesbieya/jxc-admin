<template>
    <div>
        <abstract-table v-loading="loading" :data="data" :highlight-current-row="false">
            <el-table-column align="center" label="#" type="index" width="80"/>
            <el-table-column align="center" label="操作人" prop="uname"/>
            <el-table-column align="center" label="操作类型" prop="type"/>
            <el-table-column align="center" label="时 间" prop="time"/>
            <el-table-column align="center" label="备注" prop="info"/>
        </abstract-table>

        <abstract-pagination :model="searchForm" @current-change="pageChange"/>
    </div>
</template>

<script>
import AbstractPagination from '@/component/abstract/Pagination'
import AbstractTable from "@/component/abstract/Table"
import {search} from "@/api/doc/history"
import {isEmpty, timeFormat} from "@/util"

export default {
    name: "DocHistory",

    components: {AbstractPagination, AbstractTable},

    props: {id: String},

    data() {
        return {
            loading: false,
            searchForm: {
                page: 1,
                pageSize: 15,
                total: 0
            },
            data: []
        }
    },

    watch: {
        id: {
            immediate: true,
            handler() {
                this.search()
            }
        }
    },

    methods: {
        search() {
            if (isEmpty(this.id)) return
            search
                .request({pid: this.id, ...this.searchForm})
                .then(({data: {list, total}}) => {
                    this.transformData(list)
                    this.data = list
                    this.total = total
                })
        },

        pageChange(v) {
            this.searchForm.page = v
            this.search()
        },

        transformData(data) {
            data.forEach(i => {
                i.time = timeFormat(null, new Date(i.time))
                let type = null
                switch (i.type) {
                    case 0:
                        type = '撤回'
                        break
                    case 1:
                        type = '提交'
                        break
                    case 2:
                        type = '通过'
                        break
                    case 3:
                        type = '驳回'
                        break
                }
                i.type = type
            })
        }
    }
}
</script>

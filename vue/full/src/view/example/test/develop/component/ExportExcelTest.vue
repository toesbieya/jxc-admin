<template>
    <div class="tip-row">
        测试纯前端导出excel性能，超过1w行浏览器将会有卡顿感，请勿作死<br>
        导出耗时：{{ cost }}毫秒
        <el-row>
            <el-input-number v-model="rows" :min="1" :step="1" step-strictly/>
            <el-radio v-model="merge" :label="true" style="margin-left: 20px">合并</el-radio>
            <el-radio v-model="merge" :label="false">不合并</el-radio>
            <el-button :loading="loading" type="primary" @click="exportExcel">点我测试</el-button>
        </el-row>
    </div>
</template>

<script>
import {workbook2excel, json2workbook} from "@/util/excel"

export default {
    name: "ExportExcelTest",

    data() {
        return {
            loading: false,
            columns: [
                {header: ['合并表头演示', '序号'], prop: 'no', merge: true},
                {header: ['合并表头演示', '名称'], prop: 'name', merge: true},
                {header: '日期', prop: 'date'},
            ],
            row: {
                no: 1,
                name: '老王',
                date: '20190201'
            },
            rows: 1000,
            merge: false,
            cost: 0
        }
    },

    methods: {
        exportExcel() {
            if (this.loading) return

            this.loading = true
            const start = Date.now()
            const arr = new Array(this.rows).fill(this.row)

            const workbook = json2workbook(
                arr,
                this.columns,
                this.merge ? {primaryKey: 'name', orderKey: 'no'} : null)

            workbook2excel(workbook, '测试导出.xlsx')

            this.cost = Date.now() - start
            this.loading = false
        },
    },
}
</script>

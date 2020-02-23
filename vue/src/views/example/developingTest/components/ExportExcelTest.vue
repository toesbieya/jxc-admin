<template>
    <div class="tip-row">
        测试纯前端导出excel性能，超过1w行浏览器将会有卡顿感，请勿作死<br>
        导出耗时：{{cost}}毫秒
        <el-row>
            <el-input-number v-model="rows" :min="1" :step="1" step-strictly/>
            <el-radio v-model="merge" :label="true" style="margin-left: 20px">合并</el-radio>
            <el-radio v-model="merge" :label="false">不合并</el-radio>
            <el-button :loading="loading" type="primary" @click="exportExcel">点我测试</el-button>
        </el-row>
    </div>
</template>

<script>
    import {exportExcelByJs, json2sheet} from "@/utils/excel"

    export default {
        name: "ExportExcelTest",
        data() {
            return {
                loading: false,
                column: [
                    {header: '序号', alias: 'no', merge: true},
                    {header: '名称', alias: 'name', merge: true},
                    {header: '日期', alias: 'date'},
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
                const start = Date.now()
                this.loading = true
                const arr = new Array(this.rows).fill(this.row)

                const sheet = json2sheet(this.column, arr, this.merge ? {primaryKey: 'name', orderKey: 'no'} : null)
                exportExcelByJs(sheet, '测试导出.xlsx')

                this.loading = false
                const end = Date.now()
                this.cost = end - start
            },
        },
    }
</script>

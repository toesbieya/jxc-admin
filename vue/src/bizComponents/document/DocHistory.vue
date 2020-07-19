<template>
    <el-card header="单据历史" style="margin-top: 16px">
        <abstract-table v-loading="loading" :data="data" :highlight-current-row="false">
            <el-table-column align="center" label="#" type="index" width="80"/>
            <el-table-column align="center" label="操作人" prop="uname"/>
            <el-table-column align="center" label="操作类型" prop="type"/>
            <el-table-column align="center" label="时 间" prop="time"/>
            <el-table-column align="center" label="备注" prop="info"/>
        </abstract-table>
    </el-card>
</template>

<script>
    import AbstractTable from "@/components/AbstractTable"
    import {getDocumentHistoryByPid} from "@/api/documentHistory"
    import {isEmpty, timeFormat} from "@/utils"

    export default {
        name: "DocHistory",

        components: {AbstractTable},

        props: {id: String},

        data() {
            return {
                loading: false,
                data: []
            }
        },

        watch: {
            id: {
                immediate: true,
                handler(v) {
                    if (isEmpty(v)) return
                    getDocumentHistoryByPid(v)
                        .then(history => {
                            history.forEach(i => {
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
                            this.data = history
                        })
                }
            }
        }
    }
</script>

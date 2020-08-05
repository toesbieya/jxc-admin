import tableMixin from '@/mixin/tablePageMixin'
import LinerProgress from '@/component/LinerProgress'
import {isEmpty} from "@/util"
import {elConfirm, elError, elSuccess} from "@/util/message"
import {auth} from "@/util/auth"
import {exportExcel} from "@/util/excel"

export const commonMethods = {
    getStatus(status) {
        switch (status) {
            case 0:
                return '拟定'
            case 1:
                return '待审核'
            case 2:
                return '已审核'
        }
        return null
    },
    getFinish(finish) {
        switch (finish) {
            case 0:
                return '未开始'
            case 1:
                return '进行中'
            case 2:
                return '已完成'
        }
        return null
    }
}

export default {
    mixins: [tableMixin],

    components: {LinerProgress},

    data() {
        return {
            searchForm: {
                idFuzzy: null,
                cname: null,
                vname: null
            },
            temp: {
                status: [],
                ctime: [],
                vtime: []
            }
        }
    },

    computed: {
        canAdd() {
            return auth(`${this.baseUrl}/add`)
        },
        canUpdate() {
            return auth(`${this.baseUrl}/update`)
                || auth(`${this.baseUrl}/withdraw`)
                || auth(`${this.baseUrl}/pass`)
                || auth(`${this.baseUrl}/reject`)
        },
        canDel() {
            return auth(`${this.baseUrl}/del`)
        },
        canExport() {
            return auth(`${this.baseUrl}/export`)
        },
    },

    methods: {
        ...commonMethods,
        search() {
            if (this.config.loading) return
            this.config.loading = true
            //折叠所有行
            this.tableData.forEach(row => this.$refs.table.toggleRowExpansion(row, false))
            this.row = null
            this.api.search(this.mergeSearchForm())
                .then(({list, total}) => {
                    list.forEach(i => {
                        i._loading = false //加载状态
                        i._loaded = false //是否已经加载完成
                    })
                    this.searchForm.total = total
                    this.tableData = list
                })
                .finally(() => this.config.loading = false)
        },
        getSubList(row) {
            if (row._loaded || row._loading) return
            row._loading = true
            this.api.getSubById(row.id)
                .then(data => {
                    row.data = data
                    row._loaded = true
                })
                .finally(() => row._loading = false)
        },
        add() {
            this.row = null
            this.$router.push(`${this.$route.path}/detail/add`)
        },
        see() {
            if (isEmpty(this.row)) return elError('请选择要查看的单据')
            this.$router.push(`${this.$route.path}/detail/see/${this.row.id}`)
        },
        edit() {
            if (isEmpty(this.row)) return elError('请选择要编辑的单据')
            this.$router.push(`${this.$route.path}/detail/edit/${this.row.id}`)
        },
        del() {
            if (isEmpty(this.row)) {
                return elError('请选择要删除的单据')
            }
            if (this.row.status !== 0) {
                return elError('只有状态为【拟定】时才能删除')
            }
            if (this.config.operating) return

            elConfirm(`确定删除单据【${this.row.id}】？`)
                .then(() => {
                    this.config.operating = true
                    return this.api.del(this.row.id)
                })
                .then(() => {
                    elSuccess('删除成功')
                    this.search()
                })
                .finally(() => this.config.operating = false)
        },
        downloadExcel() {
            exportExcel(`${this.baseUrl}/export`, this.mergeSearchForm(), this.excel)
        }
    }
}

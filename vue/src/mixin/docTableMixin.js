import tableMixin from '@/mixin/tablePageMixin'
import SearchFormItem from "@/component/form/Search/item"
import LinerProgress from '@/component/LinerProgress'
import ListPage from '@/view/app/common/ListPage'
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

    components: {SearchFormItem, LinerProgress, ListPage},

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
            return auth(this.api.add.url)
        },
        canUpdate() {
            return auth(this.api.update.url)
                || auth(this.api.withdraw.url)
                || auth(this.api.pass.url)
                || auth(this.api.reject.url)
        },
        canDel() {
            return auth(this.api.del.url)
        },
        canExport() {
            return auth(this.api.exportExcel.url)
        },

        buttonGroup() {
            return [
                {icon: 'el-icon-search', type: 'primary', e: this.search, content: '查 询'},
                this.canAdd && {icon: 'el-icon-plus', type: 'primary', e: this.add, content: '添 加'},
                {icon: 'el-icon-view', type: 'primary', e: this.see, content: '查 看'},
                this.canUpdate && {icon: 'el-icon-edit', type: 'primary', e: this.edit, content: '编 辑'},
                this.canDel && {icon: 'el-icon-delete', type: 'danger', e: this.del, content: '删 除'},
                this.canExport && {icon: 'el-icon-download', type: 'info', e: this.downloadExcel, content: '导 出'}
            ]
        },
        listPageConfig() {
            return {
                loading: this.config.loading,
                tableConfig: {
                    props: {data: this.tableData},
                    on: {'row-click': this.rowClick, 'expand-change': this.getSubList}
                },
                paginationConfig: {
                    props: {model: this.searchForm},
                    on: {'current-change': this.pageChange}
                }
            }
        }
    },

    methods: {
        ...commonMethods,

        search() {
            if (this.config.loading) return
            this.config.loading = true
            //折叠所有行
            this.tableData.forEach(row => this.$refs.table.toggleRowExpansion(row, false))
            this.row = null
            this.api.search
                .request(this.mergeSearchForm())
                .then(({data: {list, total}}) => {
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
            this.api.getSubById
                .request(row.id)
                .then(({data}) => {
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
                    return this.api.del.request(this.row.id)
                })
                .then(() => {
                    elSuccess('删除成功')
                    this.search()
                })
                .finally(() => this.config.operating = false)
        },

        downloadExcel() {
            exportExcel(this.api.exportExcel.url, this.mergeSearchForm(), this.excel)
        }
    }
}

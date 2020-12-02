import tableMixin from '@/mixin/tablePageMixin'
import LinerProgress from '@/component/LinerProgress'
import ListPage from '@/view/_common/ListPage'
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

    components: {LinerProgress, ListPage},

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

        listPageConfig() {
            return {
                pageLoading: this.config.operating,
                buttons: [
                    this.canAdd && {icon: 'el-icon-plus', e: this.add, content: '添 加'},
                    {icon: 'el-icon-view', e: this.see, content: '查 看'},
                    this.canUpdate && {icon: 'el-icon-edit', e: this.edit, content: '编 辑'},
                    this.canDel && {icon: 'el-icon-delete', e: this.del, content: '删 除'},
                    this.canExport && {icon: 'el-icon-download', e: this.downloadExcel, content: '导 出'}
                ],
                dataLoading: this.config.loading,
                search: {
                    props: {model: this.searchForm},
                    on: {search: this.search, reset: v => this.searchForm = v}
                },
                table: {
                    props: {data: this.tableData},
                    on: {'row-click': this.rowClick, 'expand-change': this.getSubList}
                },
                pagination: {
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
            this.tableData.forEach(row => this.$children[0].$refs.table.toggleRowExpansion(row, false))
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

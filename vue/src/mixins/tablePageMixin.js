/*
* 列表页通用混入
* 引用者必须要有search方法
* */
import AbstractTable from '@/components/AbstractTable'

const mixin = {
    data() {
        return {
            searchForm: {
                page: 1,
                pageSize: 15,
                total: 0
            },
            config: {
                loading: false,
                operating: false
            },
            tableData: [],
            row: null,
            type: 'see'
        }
    },
    components: {AbstractTable},
    watch: {
        row(v) {
            !v && this.$refs.table && this.$refs.table.setCurrentRow()
        }
    },
    methods: {
        pageChange(v) {
            this.searchForm.page = v
            this.search()
        },
    },
    mounted() {
        this.search()
    }
}

export default mixin

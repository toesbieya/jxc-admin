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

    computed: {
        tablePageNeedSearchMap() {
            return this.$store.state.needSearch.map
        }
    },

    watch: {
        row(v) {
            !v && this.$refs.table && this.$refs.table.setCurrentRow()
        },

        tablePageNeedSearchMap: {
            deep: true,
            handler(v) {
                if (this._routePath === this.$route.path){
                    this.reSearch(v)
                }
            }
        }
    },

    methods: {
        rowClick(row) {
            if (this.row === row) {
                this.$refs.table.setCurrentRow()
                this.row = null
            }
            else this.row = row
        },

        pageChange(v) {
            this.searchForm.page = v
            this.search()
        },

        reSearch(map) {
            const path = this._routePath
            if (map.hasOwnProperty(path) && map[path]) {
                this.search()
                this.$store.commit('needSearch/renew', path)
            }
        }
    },

    activated() {
        this.reSearch(this.tablePageNeedSearchMap)
    },

    mounted() {
        //注册needSearch事件
        this._routePath = this.$route.path
        this.$store.commit('needSearch/init', this._routePath)

        this.search()
    }
}

export default mixin

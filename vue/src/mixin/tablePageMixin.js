/*
* 列表页通用混入
* 引用者必须要有search方法
* */
import AbstractPagination from '@/component/abstract/Pagination'
import AbstractTable from '@/component/abstract/Table'
import {findComponentByTag} from "@/util/vue"

const mixin = {
    components: {AbstractPagination, AbstractTable},

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

    computed: {
        tablePageNeedSearchMap() {
            return this.$store.state.needSearch.map
        }
    },

    watch: {
        row(v) {
            !v && this.$_getElTableInstance().setCurrentRow()
        },

        tablePageNeedSearchMap: {
            deep: true,
            handler(v) {
                if (this._routePath === this.$route.path) {
                    this.reSearch(v)
                }
            }
        }
    },

    methods: {
        $_getElTableInstance() {
            if (!this.$_elTableInstance) {
                this.$_elTableInstance = findComponentByTag(this, 'el-table')
            }

            return this.$_elTableInstance
        },

        rowClick(row) {
            if (this.row === row) {
                this.$_getElTableInstance().setCurrentRow()
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

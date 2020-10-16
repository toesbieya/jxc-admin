/**
 * 列表页自适应混入
 * 监听el-table，动态设置max-height属性
 */

import cssVariables from "@/asset/style/variables.scss"
import {debounce} from "@/util"

export default {
    data() {
        return {
            tableMaxHeight: undefined
        }
    },

    methods: {
        resize() {
            const clientHeight = window.innerHeight
            const pageViewMargin = parseFloat(cssVariables['pageViewMargin'])

            //计算表格底部距离视窗底部的距离
            const tableRect = this.$refs.table.$el.getBoundingClientRect()
            const bottomDistanceBetweenWindowAndTable = clientHeight - tableRect.top - tableRect.height

            //判断有无分页组件，分页组件总高度为70px（本身50px高度，margin-top 20px）
            const paginationHeight = this.$el.querySelector('.table-container > .el-pagination') ? 70 : 0

            //表格距离视窗底部的理想距离（分页高度 + el-card的1px边距 + el-card padding + 页面margin）
            const bottomDistanceBetweenWindowAndTableShouldBe =
                paginationHeight
                + 1
                + 20
                + pageViewMargin

            const overHeight = bottomDistanceBetweenWindowAndTableShouldBe - bottomDistanceBetweenWindowAndTable
            if (overHeight > 0) {
                this.tableMaxHeight = tableRect.height - overHeight
            }
        },
        createResizeObserver() {
            if (this.resizeObserver) return

            this.resizeObserver = new ResizeObserver(this.resize)
            this.resizeObserver.observe(this.$refs.table.$el)

            this.$once('hook:beforeDestroy', () => {
                this.resizeObserver && this.resizeObserver.disconnect()
            })
        },
    },

    mounted() {
        this.resize = debounce(this.resize, 300)
        this.createResizeObserver()
    }
}

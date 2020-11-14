/**
 * 列表页自适应混入
 * 监听el-table，动态设置max-height属性
 */

import cssVariables from "@/asset/style/variables.scss"
import {debounce} from "@/util"

export default {
    data() {
        return {
            tableMaxHeight: undefined,
            desiredDistance: undefined
        }
    },

    methods: {
        //设置表格距离视窗底部的理想距离（分页高度 + el-card的1px边距 + el-card padding + 页面margin）
        calcDesiredDistance() {
            const pageViewMargin = parseFloat(cssVariables['pageViewMargin'])
            const paginationHeight = this.$el.querySelector('.table-container > .el-pagination') ? 50 : 0
            this.desiredDistance = paginationHeight + 1 + 20 + pageViewMargin
        },

        resize() {
            const clientHeight = window.innerHeight

            //计算表格底部距离视窗底部的距离
            const tableRect = this.$refs.table.$el.getBoundingClientRect()
            const distance = clientHeight - tableRect.top - tableRect.height

            if (this.desiredDistance === undefined) {
                this.calcDesiredDistance()
            }

            const overHeight = this.desiredDistance - distance
            this.tableMaxHeight = tableRect.height - overHeight
        },
        createResizeObserver() {
            if (this.resizeObserver) return

            this.resizeObserver = new ResizeObserver(this.resize)
            this.resizeObserver.observe(this.$el)

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

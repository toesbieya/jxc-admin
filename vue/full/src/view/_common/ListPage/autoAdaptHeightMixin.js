/**
 * 列表页自适应混入
 * 监听el-table，动态设置max-height属性
 */

import {debounce} from "@/util"
import {findComponentByTag} from "@/util/vue"

export default {
    data() {
        return {
            //el-table的max-height属性
            tableMaxHeight: undefined,
            //上一次的maxHeight，解决resize时表格高度不正确
            lastTableMaxHeight: undefined,

            //表格距离卡片容器底部的理想距离
            expectedDistance: undefined
        }
    },

    methods: {
        //设置表格距离卡片容器底部的理想距离（分页高度 + el-card的1px边距 + el-card padding）
        calcExpectedDistance() {
            const paginationHeight = this.$el.querySelector('.table-container > .el-pagination') ? 50 : 0
            this.expectedDistance = paginationHeight + 1 + 20
        },

        resize() {
            const clientHeight = window.innerHeight

            //计算卡片容器底部距离视窗底部的距离
            const containerRect = this.$el.getBoundingClientRect()
            const containerDistance = clientHeight - containerRect.top - containerRect.height

            //计算表格底部距离视窗底部的距离
            const tableRect = this.$refs.table.$el.getBoundingClientRect()
            const tableDistance = clientHeight - tableRect.top - tableRect.height

            if (this.expectedDistance === undefined) {
                this.calcExpectedDistance()
            }

            const overHeight = containerDistance + this.expectedDistance - tableDistance

            //overHeight为0说明表格刚好在卡片容器内
            if (overHeight === 0) {
                this.tableMaxHeight = this.lastTableMaxHeight
            }
            else {
                this.lastTableMaxHeight = this.tableMaxHeight
                this.tableMaxHeight = tableRect.height - overHeight
            }
        },
    },

    mounted() {
        //resize必须异步，否则新版chrome(87.0.4280.88)会出问题
        this.resize = debounce(this.resize, 0)

        this.resizeObserver = new ResizeObserver(this.resize)
        this.resizeObserver.observe(this.$el)
        const searchFormInstance = findComponentByTag(this, 'search-form')
        if (searchFormInstance) {
            this.resizeObserver.observe(searchFormInstance.$el)
        }

        this.$once('hook:beforeDestroy', () => {
            this.resizeObserver && this.resizeObserver.disconnect()
        })
    }
}

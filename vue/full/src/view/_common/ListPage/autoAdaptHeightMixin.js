/**
 * 列表页自适应混入
 * 监听el-table，动态设置max-height属性
 */

export default {
    props: {
        //容器距离视窗底部的理想距离，默认是页面margin+页脚高度
        bottomDistance: {type: Number, default: 24 + 64}
    },

    data() {
        return {
            //el-table的max-height属性
            tableMaxHeight: undefined
        }
    },

    methods: {
        //位于表格底部与容器底部之间元素的高度，目前只考虑分页
        getExtraHeight() {
            //el-card的1px边距 + el-card padding
            const extra = 1 + 20
            const pagination = this.$el.querySelector('.el-pagination')

            return pagination ? extra + pagination.offsetHeight : extra
        },

        resize() {
            //被keep-alive缓存后，由activated -> deactivated时，_inactive为true
            //此时一般是路由向另一个页面切换，不进行后续操作
            if (this._inactive === true) return

            if (this.thisResizeIsTriggeredByLast()) {
                return this.removeTriggerMark()
            }

            const clientHeight = window.innerHeight

            const rect = this.$el.querySelector('.el-table').getBoundingClientRect()

            //表格顶部距离视窗底部的距离
            const top = clientHeight - rect.top

            //期望的表格底部距离视窗底部的距离
            const expected = this.bottomDistance + this.getExtraHeight()

            //当前表格理论上的最大高度，不一定会应用
            const newVal = top - expected
            const oldVal = this.tableMaxHeight

            //表格最大高度并未超出期望
            if (oldVal === undefined && newVal >= rect.height) {
                return
            }

            if (oldVal !== newVal) {
                this.willTriggeredNextResize()
                this.tableMaxHeight = newVal
            }
        },

        thisResizeIsTriggeredByLast() {
            return this.$_hasResize === true
        },
        willTriggeredNextResize() {
            this.$_hasResize = true
        },
        removeTriggerMark() {
            this.$_hasResize = false
        }
    },

    mounted() {
        //为了用户体验，不使用防抖
        this.$_resizeObserver = new ResizeObserver(this.resize)
        this.$_resizeObserver.observe(this.$el)

        //卸载resizeObserver
        this.$once('hook:beforeDestroy', () => {
            if (this.$_resizeObserver) {
                this.$_resizeObserver.disconnect()
                this.$_resizeObserver = null
            }
        })
    }
}

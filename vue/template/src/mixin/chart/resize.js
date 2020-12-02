import {debounce} from '@/util'

export default {
    methods: {
        $_initResizeEvent() {
            this.resizeObserver = new ResizeObserver(this.$_resizeHandler)
            this.resizeObserver.observe(this.$el)
        },
        $_destroyResizeEvent() {
            if (this.resizeObserver) {
                this.resizeObserver.disconnect()
                this.resizeObserver = null
            }
        }
    },

    mounted() {
        this.$_resizeHandler = debounce(() => this.chart && this.chart.resize())
        this.$_initResizeEvent()
        this.$nextTick(() => this.$_resizeHandler())
        this.$once('hook:deactivated', this.$_destroyResizeEvent)
        this.$once('hook:beforeDestroy', this.$_destroyResizeEvent)
    },

    activated() {
        this.$_initResizeEvent()
        this.$nextTick(() => this.$_resizeHandler())
    }
}

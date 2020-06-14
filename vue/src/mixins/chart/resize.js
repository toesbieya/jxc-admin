import {debounce} from '@/utils'

export default {
    data() {
        return {
            $_sidebarElm: null,
            $_resizeHandler: null
        }
    },
    methods: {
        $_initResizeEvent() {
            window.addEventListener('resize', this.$_resizeHandler)
        },
        $_destroyResizeEvent() {
            window.removeEventListener('resize', this.$_resizeHandler)
        }
    },
    mounted() {
        this.$_resizeHandler = debounce(() => this.chart && this.chart.resize())
        this.$_initResizeEvent()
        this.$nextTick(() => this.$_resizeHandler())
    },
    beforeDestroy() {
        this.$_destroyResizeEvent()
    },
    activated() {
        this.$_initResizeEvent()
        this.$nextTick(() => this.$_resizeHandler())
    },
    deactivated() {
        this.$_destroyResizeEvent()
    }
}

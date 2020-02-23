import {isEmpty, waitUntilSuccess} from "@/utils"

export default {
    props: {
        width: {
            type: String,
            default: '100%'
        },
        height: {
            type: String,
            default: '350px'
        }
    },
    data() {
        return {
            loading: false,
            chart: null
        }
    },
    methods: {
        $_getChartInstance() {
            return waitUntilSuccess(
                () => !isEmpty(window.echarts),
                () => this.chart = window.echarts.init(this.$_getChartDom(), 'macarons')
            )
        },
        $_getChartDom() {
            return this.$el
        }
    },
    mounted() {
        this.$_getChartInstance().then(() => this.init())
    },
    beforeDestroy() {
        if (!this.chart) return
        this.chart.dispose()
        this.chart = null
    }
}

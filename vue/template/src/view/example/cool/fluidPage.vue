<template>
    <div style="margin: -24px">
        <canvas :height="height" :width="width" id="canvas-fluid" style="display: block"/>
    </div>
</template>

<script>
import {debounce} from "@/util"

export default {
    name: "fluid",

    data() {
        return {
            sign: true,
            width: 1200,
            height: 800
        }
    },

    methods: {
        $_initResizeEvent() {
            this.resizeObserver = new ResizeObserver(() => this.$_resizeHandler())
            this.resizeObserver.observe(this.parentDom)
        },
        $_destroyResizeEvent() {
            if (this.resizeObserver) {
                this.resizeObserver.disconnect()
                this.resizeObserver = null
            }
        },
        start() {
            import('@/plugin/webgl/fluid').then(_ => _.default(this.$el.querySelector('#canvas-fluid')))
        },
        close() {
            window.location.reload()
        }
    },

    mounted() {
        this.parentDom = document.querySelector('.page-main')
        this.width = this.parentDom.clientWidth
        this.height = this.parentDom.clientHeight
        this.$_resizeHandler = debounce(() => {
            this.width = this.parentDom.clientWidth
            this.height = this.parentDom.clientHeight
        })
        this.$_initResizeEvent()
        this.$nextTick(() => this.start())
    },

    beforeDestroy() {
        this.$_destroyResizeEvent()
        this.close()
    }
}
</script>

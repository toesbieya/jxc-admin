<template>
    <div style="position: absolute;top: 0;right: 0;left: 0;bottom: 0;">
        <canvas :height="height" :width="width" id="canvas-fluid" style="display: block"/>
    </div>
</template>

<script>
    import {debounce} from "@/utils"

    export default {
        name: "fluid",

        data() {
            return {
                sign: true,
                width: 1200,
                height: 800,
                $_resizeHandler: null,
                parentDom: null
            }
        },

        methods: {
            $_initResizeEvent() {
                window.addEventListener('resize', this.$_resizeHandler)
            },
            $_destroyResizeEvent() {
                window.removeEventListener('resize', this.$_resizeHandler)
            },
            start() {
                import('@/plugin/webgl/fluid').then(_ => _.default(this.$el.querySelector('#canvas-fluid')))
            },
            close() {
                window.location.reload()
            }
        },

        mounted() {
            this.parentDom = document.querySelector('.app-main')
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

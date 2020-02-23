<template>
    <div @click="change">
        <i :class="icon" class="navbar-icon"/>
    </div>
</template>

<script>
    //参考iview-admin https://admin.iviewui.com
    export default {
        name: "Fullscreen",
        props: {
            value: {
                type: Boolean,
                default: false
            },
            events: {
                type: Array,
                default: () => ['fullscreenchange', 'mozfullscreenchange', 'webkitfullscreenchange', 'msfullscreenchange']
            }
        },
        computed: {
            icon() {
                return this.value ? 'el-icon-aim' : 'el-icon-full-screen'
            }
        },
        methods: {
            change() {
                let main = document.body
                if (this.value) {
                    if (document.exitFullscreen) {
                        document.exitFullscreen()
                    }
                    else if (document.mozCancelFullScreen) {
                        document.mozCancelFullScreen()
                    }
                    else if (document.webkitCancelFullScreen) {
                        document.webkitCancelFullScreen()
                    }
                    else if (document.msExitFullscreen) {
                        document.msExitFullscreen()
                    }
                }
                else {
                    if (main.requestFullscreen) {
                        main.requestFullscreen()
                    }
                    else if (main.mozRequestFullScreen) {
                        main.mozRequestFullScreen()
                    }
                    else if (main.webkitRequestFullScreen) {
                        main.webkitRequestFullScreen()
                    }
                    else if (main.msRequestFullscreen) {
                        main.msRequestFullscreen()
                    }
                }
            },
            emit() {
                this.$emit('input', !this.value)
                this.$emit('on-change', !this.value)
            },
            bindEvent() {
                this.events.forEach(e => document.addEventListener(e, this.emit))
            },
            removeEvent() {
                this.events.forEach(e => document.removeEventListener(e, this.emit))
            }
        },
        mounted() {
            let isFullscreen =
                document.fullscreenElement
                || document.mozFullScreenElement
                || document.webkitFullscreenElement
                || document.fullScreen
                || document.mozFullScreen
                || document.webkitIsFullScreen
            isFullscreen = !!isFullscreen
            this.bindEvent()
            this.$emit('input', isFullscreen)
        },
        beforeDestroy() {
            this.removeEvent()
        }
    }
</script>

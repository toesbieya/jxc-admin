<template>
    <div>
        <div class="tip-row">
            <a href="https://github.com/stevenjoezhang/live2d-widget" target="_blank">
                l2d看板娘
            </a>
        </div>
    </div>
</template>

<script>
import {loadExternalResource} from "@/util/browser"

export default {
    name: "l2d",

    data: () => ({l2d: null}),

    mounted() {
        const path = `${process.env.BASE_URL}static/live2d/`

        Promise.all([
            import('@/plugin/live2d'),
            loadExternalResource(path + "waifu.css", "css"),
            loadExternalResource(path + "live2d.min.js")
        ])
            .then(([_]) => {
                this.l2d = new _.default(`${path}waifu-tips.json`, "https://live2d.fghrsh.net/api")
            })
    },

    beforeDestroy() {
        this.l2d && this.l2d.exit()
    }
}
</script>

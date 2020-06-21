<template>
    <el-image-viewer v-if="value" :initial-index="index" :on-close="close" :url-list="urlList" :z-index="200000"/>
</template>

<script>
    import ImageViewer from 'element-ui/packages/image/src/image-viewer'

    export default {
        name: "ImageViewer",

        components: {ElImageViewer: ImageViewer},

        data() {
            return {
                value: false,
                index: 0,
                urlList: []
            }
        },

        methods: {
            close() {
                this.value = false
            }
        }
    }
</script>

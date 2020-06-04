<template>
    <div>
        <div class="tip-row">
            <span>PhotoSwipe演示</span>
        </div>
        <div>
            <img v-for="(img,index) in data" :src="img.src" @click="showPhoto(index)"/>
        </div>
        <PhotoSwipe ref="photoSwipe" :data="data" :index="photoSwipeIndex"/>

        <div class="tip-row">
            <span>v-viewer演示</span>
        </div>
        <div v-viewer.static>
            <img v-for="(img,index) in data" :src="img.src" :alt="`hello ${index}`"/>
        </div>

        <div class="tip-row">
            <span>element-ui自带预览组件演示</span>
        </div>
        <div>
            <img v-for="(img,index) in data" :src="img.src" @click="showElImageViewer(index)"/>
        </div>
    </div>
</template>

<script>
    import PhotoSwipe from "./PhotoSwipe"
    import Vue from 'vue'
    import Viewer from 'v-viewer'

    Vue.use(Viewer)

    export default {
        name: "picturePreviewExample",
        components: {PhotoSwipe},
        data() {
            return {
                data: [
                    {
                        src: 'https://fengyuanchen.github.io/viewerjs/images/thumbnails/tibet-1.jpg',
                        h: 1920,
                        w: 1080,
                        title: 'hello'
                    },
                    {
                        src: 'https://fengyuanchen.github.io/viewerjs/images/thumbnails/tibet-2.jpg',
                        h: 800,
                        w: 1200
                    },
                    {
                        src: 'https://fengyuanchen.github.io/viewerjs/images/thumbnails/tibet-3.jpg',
                        h: 800,
                        w: 1200
                    }
                ],
                photoSwipeIndex: 0,
                showElViewer: false,
                elViewerIndex: 0,
            }
        },
        computed: {
            previewSrcList() {
                return this.data.map(i => i.src)
            }
        },
        methods: {
            showPhoto(index) {
                this.photoSwipeIndex = index
                this.$nextTick(() => this.$refs.photoSwipe.show())
            },
            showElImageViewer(index) {
                this.$image({index, urlList: this.previewSrcList})
            }
        }
    }
</script>

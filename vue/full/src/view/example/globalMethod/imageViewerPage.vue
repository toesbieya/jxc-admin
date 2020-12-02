<template>
    <div>
        <div class="tip-row">
            这是对<a href="https://github.com/sachinchoolur/lightgallery.js" target="_blank">lightgallary</a>的简单封装
        </div>

        <div>
            <img v-for="(i,index) in images" :key="i" :src="i" @click="preview(index)">
        </div>
    </div>
</template>

<script>
export default {
    name: "imageViewerPage",

    data() {
        return {
            images: [
                'https://dss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=4018557288,1217151095&fm=26&gp=0.jpg',
                'https://dss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3252521864,872614242&fm=26&gp=0.jpg',
                'https://dss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2534506313,1688529724&fm=26&gp=0.jpg'
            ]
        }
    },

    methods: {
        preview(index) {
            this.$image({index, urlList: this.images})
        }
    }
}
</script>

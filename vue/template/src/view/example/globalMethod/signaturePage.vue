<template>
    <div>
        <div class="tip-row">
            搞了个手写签名，竖屏横屏不可兼得
        </div>
        <div>
            <img :src="src" style="width: 100%;height: 500px;object-fit: contain">
        </div>
        <el-button plain @click="signature">点击签名</el-button>
    </div>
</template>

<script>
export default {
    name: 'signaturePage',

    data() {
        return {
            src: null
        }
    },

    methods: {
        signature() {
            this.$signature({
                image: this.src, onConfirm: img => this.src = img
            })
        }
    }
}
</script>

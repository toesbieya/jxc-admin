<template>
    <div class="zoom-wrapper">
        <el-button circle icon="el-icon-minus" size="small" title="缩小" type="info" @click="zoomOut"/>
        <span class="zoom-number">{{ value }}%</span>
        <el-button circle icon="el-icon-plus" size="small" title="放大" type="info" @click="zoomIn"/>
    </div>
</template>

<script>
    export default {
        name: "ZoomControl",
        props: {
            value: {
                type: Number,
                default: 100
            },
            step: {
                type: Number,
                default: 20
            },
            min: {
                type: Number,
                default: 10
            },
            max: {
                type: Number,
                default: 200
            }
        },
        methods: {
            zoomIn() {
                const zoom = this.value + this.step
                if (zoom <= this.max) this.$emit('input', zoom)
            },
            zoomOut() {
                const zoom = this.value - this.step
                if (zoom >= this.min) this.$emit('input', zoom)
            }
        },
    }
</script>

<style lang="scss">
    .zoom-number {
        color: #657180;
        padding: 0 10px;
        display: inline-block;
        width: 60px;
        text-align: center;
    }
</style>

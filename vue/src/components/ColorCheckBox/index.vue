<template>
    <div class="color-checkbox" :style="{backgroundColor: color}" @click="click">
        <i v-if="checked" class="el-icon-check"/>
    </div>
</template>

<script>
    export default {
        name: "ColorCheckBox",

        inject: ['colorCheckGroup'],

        props: {color: String},

        computed: {
            checked() {
                const group = this.colorCheckGroup
                return group && group.value === this.color
            }
        },

        methods: {
            click() {
                if (this.checked) return
                this.colorCheckGroup.$emit('input', this.color)
            }
        },

        created() {
            const group = this.colorCheckGroup
            group && group.children.push(this.color)
        },
    }
</script>

<style>
    .color-checkbox {
        float: left;
        width: 20px;
        height: 20px;
        border-radius: 2px;
        cursor: pointer;
        margin-right: 8px;
        text-align: center;
        font-weight: bold;
        color: #fff;
    }
</style>

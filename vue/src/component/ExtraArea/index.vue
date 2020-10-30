<template>
    <el-row type="flex" justify="space-between">
        <el-col v-show="!expanded" :span="extraSpan">
            <slot name="extra"/>
        </el-col>
        <div style="display: flex;justify-content: center;align-items: center">
            <el-link :icon="iconClass" :underline="false" style="font-size: 16px" @click="expanded = !expanded"/>
        </div>
        <el-col :span="defaultSpan">
            <slot/>
        </el-col>
    </el-row>
</template>

<script>
export default {
    name: "ExtraArea",

    props: {
        extra: {type: Number, default: 5}
    },

    data: () => ({expanded: false}),

    computed: {
        extraSpan() {
            return this.expanded ? 0 : this.extra
        },
        defaultSpan() {
            return 23 - this.extraSpan
        },
        iconClass() {
            return `el-icon-arrow-${this.expanded ? 'right' : 'left'}`
        }
    }
}
</script>



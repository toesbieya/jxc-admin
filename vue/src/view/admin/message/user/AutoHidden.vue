<template>
    <div class="auto-hidden">
        <div ref="content" v-html="html" class="auto-hidden__content" :style="contentStyle"/>
        <div v-if="showCollapse" class="auto-hidden__collapse">
            <el-button
                type="text"
                size="mini"
                :icon="collapseIcon"
                @click="handleCollapse"
            >
                {{ collapse ? '展开' : '收起' }}
            </el-button>
        </div>
    </div>
</template>

<script>
import {getElementHeight} from "@/util/browser"

export default {
    name: "AutoHidden",
    props: {
        html: String,
        height: {type: String, default: '100px'}
    },
    data() {
        return {
            showCollapse: false,
            collapse: true
        }
    },
    computed: {
        contentStyle() {
            if (!this.showCollapse || !this.collapse) return {}
            return {'max-height': this.height}
        },
        collapseIcon() {
            return this.collapse ? 'el-icon-arrow-down' : 'el-icon-arrow-up'
        }
    },
    methods: {
        handleCollapse() {
            this.collapse = !this.collapse
        },
        isOverHeight() {
            const height = getElementHeight(this.$refs['content'])
            return height > parseFloat(this.height)
        }
    },
    mounted() {
        this.showCollapse = this.isOverHeight()
    }
}
</script>

<style lang="scss">
.auto-hidden {
    position: relative;

    &__content {
        overflow: hidden;
    }

    &__collapse {
        margin: 0 auto;
        text-align: center;
    }
}
</style>

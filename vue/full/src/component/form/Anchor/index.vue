<template>
    <div class="form-anchor">
        <div
            v-for="i in data"
            :key="i.ref"
            :class="{'form-anchor-item': true,'active': i.ref === cur}"
            @click="() => click(i.ref)"
        >
            {{ i.label }}
        </div>
        <div class="form-anchor-item" @click="close">关闭导航</div>
    </div>
</template>

<script>
import {debounce} from '@/util'
import {getScroll, getTopDistance, scrollTo} from '@/util/browser'

export default {
    name: 'FormAnchor',

    props: {
        reference: Function,

        //锚点链接数组，{ref：链接对应的ref，使用reference()[ref]取得对应的dom, label：链接名称}
        data: {type: Array, default: () => []},

        //距离窗口顶部达到指定偏移量后触发，单位px
        offsetTop: {type: Number, default: 15},

        //锚点区域边界，单位px
        bounds: {type: Number, default: 0},

        //指定滚动的容器
        getContainer: {
            type: Function,
            default: () => document.querySelector('#app .page-main>.page-content')
        },
    },

    data: () => ({cur: null}),

    methods: {
        //彻底销毁本组件
        close() {
            this.$el.remove()
            this.$destroy()
        },

        //获取当前激活的锚点链接的ref值
        getCur(offsetTop, bounds) {
            const sections = []
            const container = this.getContainer()
            for (const {ref} of this.data) {
                const target = this.getDomFromRef(ref)
                if (!target) continue

                const top = getTopDistance(target, container)
                if (top <= offsetTop + bounds) {
                    sections.push({ref, top})
                }
            }

            //可能有多个满足条件的dom，取距滚动容器最远的那个
            if (sections.length) {
                const max = sections.reduce((prev, curr) => (curr.top > prev.top ? curr : prev))
                return max.ref
            }

            return null
        },

        getDomFromRef(ref) {
            const refs = this.reference()

            if (!refs) return null

            const target = Array.isArray(refs[ref]) ? refs[ref][0] : refs[ref]

            //可能是vue组件实例，也可能是dom
            return target.$el || target
        },

        click(ref) {
            if (!ref || this.animating || !this.reference) return

            this.cur = ref

            const targetElement = this.getDomFromRef(ref)
            if (!targetElement) return

            this.animating = true

            const container = this.getContainer()
            const scrollTop = getScroll(container, true)
            const elOffsetTop = getTopDistance(targetElement, container)
            const y = scrollTop + elOffsetTop - this.offsetTop

            //由于滚动监听函数的防抖阈值是100ms，所以延迟设置滚动标识
            scrollTo(container, y, {callback: () => window.setTimeout(() => this.animating = false, 110)})
        },

        scroll() {
            if (this.animating || !this.reference) return
            this.cur = this.getCur(this.offsetTop, this.bounds)
        }
    },

    mounted() {
        this.scroll = debounce(this.scroll)

        this.scroll()

        const container = this.getContainer()

        container.addEventListener('scroll', this.scroll)
        this.$once('hook:beforeDestroy', () => {
            container.removeEventListener('scroll', this.scroll)
        })
    }
}
</script>

<style lang="scss">
@import "~@/style/mixin.scss";
@import "~@/style/var";

.form-anchor {
    position: fixed;
    right: 0;
    top: 200px;
    min-width: 72px;
    border-radius: 4px 0 0 4px;
    background-color: $--background-color-base;
    @include deep-shadow;
    z-index: 100;

    &-item {
        width: inherit;
        height: 28px;
        line-height: 28px;
        text-align: center;
        color: $--color-text-secondary;
        padding: 0 10px;
        font-size: 12px;
        cursor: pointer;

        &:hover,
        &.active {
            color: $--color-primary
        }

        &:not(:last-child) {
            border-bottom: $--border-base;
        }
    }
}
</style>

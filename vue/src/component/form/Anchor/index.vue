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
import {debounce} from "@/util"
import {getScroll, getOffsetTop, scrollTo} from "@/util/browser"

export default {
    name: "FormAnchor",

    props: {
        reference: Function,
        data: {type: Array, default: () => []},
        offsetTop: {type: Number, default: 15},
        bounds: {type: Number, default: 0},
        getContainer: {
            type: Function,
            default: () => document.querySelector('#app .router-main>.scroll-container')
        },
    },

    data: () => ({cur: null}),

    methods: {
        close() {
            this.$el.remove()
            this.$destroy()
        },

        getCur(offsetTop, bounds) {
            const sections = []
            const container = this.getContainer()
            for (const {ref} of this.data) {
                const target = this.getDomFromRef(ref)
                if (!target) continue
                const top = getOffsetTop(target, container)
                if (top <= offsetTop + bounds) {
                    sections.push({ref, top})
                }
            }
            if (sections.length) {
                const max = sections.reduce((prev, curr) => (curr.top > prev.top ? curr : prev))
                return max.ref
            }
            return null
        },

        getDomFromRef(ref) {
            const refs = this.reference()
            if (!refs) return null
            return refs[ref]['$el'] || refs[ref][0]['$el']
        },

        click(ref) {
            if (!ref || this.animating || !this.reference) return
            this.cur = ref
            const {offsetTop, getContainer} = this
            const container = getContainer()
            const scrollTop = getScroll(container, true)
            const targetElement = this.getDomFromRef(ref)
            if (!targetElement) return
            const eleOffsetTop = getOffsetTop(targetElement, container)
            const y = scrollTop + eleOffsetTop - offsetTop
            this.animating = true
            scrollTo(y, {callback: () => this.animating = false, getContainer})
        },

        scroll() {
            if (this.animating || !this.reference) return
            this.cur = this.getCur(this.offsetTop, this.bounds)
        }
    },

    mounted() {
        this.scroll()

        const container = this.getContainer()
        this.handleScroll = debounce(this.scroll)

        container.addEventListener('scroll', this.handleScroll)
        this.$once('hook:beforeDestroy', () => {
            container.removeEventListener('scroll', this.handleScroll)
        })
    }
}
</script>

<style lang="scss">
@import "~@/asset/style/variables.scss";

.form-anchor {
    position: fixed;
    right: 0;
    top: 200px;
    min-width: 72px;
    border-radius: 4px 0 0 4px;
    background: #fff;
    box-shadow: 0 2px 12px 0 rgba(166, 167, 173, 0.5);
    z-index: 100;

    &-item {
        width: inherit;
        height: 28px;
        line-height: 28px;
        text-align: center;
        color: #7C7E85;
        padding: 0 10px;
        font-size: 12px;
        cursor: pointer;

        &:hover,
        &.active {
            color: $--color-primary
        }

        &:not(:last-child) {
            box-shadow: 0 1px 0 0 rgba(166, 167, 173, 0.1);
        }
    }
}
</style>

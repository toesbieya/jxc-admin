<template>
    <el-scrollbar :vertical="false" @wheel.native.prevent="handleScroll" ref="scrollContainer">
        <slot/>
    </el-scrollbar>
</template>

<script>
const tagAndTagSpacing = 4

export default {
    name: 'ScrollPane',

    data: () => ({rAF: null}),

    computed: {
        scrollWrapper() {
            return this.$refs.scrollContainer.$refs.wrap
        },
    },

    methods: {
        handleScroll(e) {
            const eventDelta = e.wheelDelta || -(e.detail || 0) * 40
            if (eventDelta > 0 && this.scrollWrapper.scrollLeft === 0) return
            if (eventDelta < 0 && this.scrollWrapper.scrollWidth <= this.scrollWrapper.scrollLeft + this.scrollWrapper.clientWidth) return
            this.smoothScroll(-eventDelta * 2)
        },

        moveToTarget(currentTag) {
            const $container = this.$refs.scrollContainer.$el
            const $containerWidth = $container.offsetWidth
            const $scrollWrapper = this.scrollWrapper
            const tagList = this.$parent.$refs.tag

            let firstTag = null
            let lastTag = null

            // find first tag and last tag
            if (tagList.length > 0) {
                firstTag = tagList[0]
                lastTag = tagList[tagList.length - 1]
            }

            if (firstTag === currentTag) {
                this.scrollLeft(0)
            }
            else if (lastTag === currentTag) {
                this.scrollLeft($scrollWrapper.scrollWidth - $containerWidth)
            }
            else {
                // find preTag and nextTag
                const currentIndex = tagList.findIndex(item => item === currentTag)
                const prevTag = tagList[currentIndex - 1]
                const nextTag = tagList[currentIndex + 1]

                // the tag's offsetLeft after of nextTag
                const afterNextTagOffsetLeft = nextTag.$el.offsetLeft + nextTag.$el.offsetWidth + tagAndTagSpacing

                // the tag's offsetLeft before of prevTag
                const beforePrevTagOffsetLeft = prevTag.$el.offsetLeft - tagAndTagSpacing

                if (afterNextTagOffsetLeft > $scrollWrapper.scrollLeft + $containerWidth) {
                    this.scrollLeft(afterNextTagOffsetLeft - $containerWidth)
                }
                else if (beforePrevTagOffsetLeft < $scrollWrapper.scrollLeft) {
                    this.scrollLeft(beforePrevTagOffsetLeft)
                }
            }
        },

        scrollLeft(val) {
            this.scrollWrapper.scrollTo({
                left: val,
                behavior: 'smooth'
            })
        },

        smoothScroll(val) {
            if (this.rAF) window.cancelAnimationFrame(this.rAF)
            let cost = 300, times = cost / 16, gap = val / times + 1
            const frameFunc = () => {
                if (times > 0) {
                    this.scrollWrapper.scrollLeft += gap
                    this.rAF = window.requestAnimationFrame(frameFunc)
                    times--
                }
                else window.cancelAnimationFrame(this.rAF)
            }
            frameFunc()
        }
    }
}
</script>

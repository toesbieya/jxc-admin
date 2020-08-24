<template>
    <el-scrollbar ref="scrollContainer" native noresize @wheel.native.prevent="handleScroll">
        <slot/>
    </el-scrollbar>
</template>

<script>
const tagAndTagSpacing = 4

export default {
    name: 'ScrollPane',

    methods: {
        getWrapper() {
            return this.$refs.scrollContainer.$refs.wrap
        },

        handleScroll(e) {
            const eventDelta = e.wheelDelta || -(e.detail || 0) * 40
            const {scrollLeft, scrollWidth, clientWidth} = this.getWrapper()
            if (eventDelta > 0 && scrollLeft === 0
                || eventDelta < 0 && scrollWidth <= scrollLeft + clientWidth) {
                return
            }
            this.smoothScroll(-eventDelta * 2)
        },

        moveToTarget(currentTag) {
            const $containerWidth = this.$refs.scrollContainer.$el.offsetWidth
            const {scrollWidth, scrollLeft} = this.getWrapper()
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
                this.scrollLeft(scrollWidth - $containerWidth)
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

                if (afterNextTagOffsetLeft > scrollLeft + $containerWidth) {
                    this.scrollLeft(afterNextTagOffsetLeft - $containerWidth)
                }
                else if (beforePrevTagOffsetLeft < scrollLeft) {
                    this.scrollLeft(beforePrevTagOffsetLeft)
                }
            }
        },

        scrollLeft(val) {
            this.getWrapper().scrollTo({
                left: val,
                behavior: 'smooth'
            })
        },

        smoothScroll(val) {
            if (this.rAF) window.cancelAnimationFrame(this.rAF)
            let cost = 300, times = cost / 16, gap = val / times + 1
            const frameFunc = () => {
                if (times > 0) {
                    this.getWrapper().scrollLeft += gap
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

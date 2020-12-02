<template>
    <div class="tags-view-wrapper" @wheel="handleScroll">
        <slot/>
    </div>
</template>

<script>
import {scrollTo} from "@/util/browser"

export default {
    name: 'ScrollPanel',

    props: {
        between: {type: Number, default: 4}
    },

    methods: {
        handleScroll(e) {
            const eventDelta = e.wheelDelta || -(e.detail || 0) * 40
            const {scrollLeft, scrollWidth, clientWidth} = this.$el
            if (eventDelta > 0 && scrollLeft === 0
                || eventDelta < 0 && scrollWidth <= scrollLeft + clientWidth) {
                return
            }
            this.smoothScroll(-eventDelta)
        },

        smoothScroll(val) {
            if (this.rAF) {
                window.cancelAnimationFrame(this.rAF)
                this.rAf = null
            }

            this.rAf = scrollTo(this.$el, this.$el.scrollLeft + val, {direction: 'left', duration: 100})
        },

        moveToTarget(currentTag, tagInstances) {
            const {offsetWidth: containerWidth, scrollWidth, scrollLeft} = this.$el

            if (containerWidth >= scrollWidth) return

            let firstTag = null
            let lastTag = null

            // find first tag and last tag
            if (tagInstances.length > 0) {
                firstTag = tagInstances[0]
                lastTag = tagInstances[tagInstances.length - 1]
            }

            if (firstTag === currentTag) {
                return this.scrollLeft(0)
            }
            if (lastTag === currentTag) {
                return this.scrollLeft(scrollWidth - containerWidth)
            }

            // find preTag and nextTag
            const currentIndex = tagInstances.findIndex(item => item === currentTag)
            const prevTag = tagInstances[currentIndex - 1]
            const nextTag = tagInstances[currentIndex + 1]

            // the tag's offsetLeft after of nextTag
            const afterNextTagOffsetLeft = nextTag.$el.offsetLeft + nextTag.$el.offsetWidth + this.between

            // the tag's offsetLeft before of prevTag
            const beforePrevTagOffsetLeft = prevTag.$el.offsetLeft - this.between

            if (afterNextTagOffsetLeft > scrollLeft + containerWidth) {
                this.scrollLeft(afterNextTagOffsetLeft - containerWidth)
            }
            else if (beforePrevTagOffsetLeft < scrollLeft) {
                this.scrollLeft(beforePrevTagOffsetLeft)
            }
        },

        scrollLeft(val) {
            this.$el.scrollTo({left: val, behavior: 'smooth'})
        }
    }
}
</script>

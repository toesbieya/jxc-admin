<template>
    <div>
        <el-popover ref="popover" :value="showPopover" trigger="manual">
            <div class="guide-popover-container">
                <div v-html="step.content"/>

                <div class="guide-popover-action">
                    <div class="action-close">
                        <el-button size="mini" type="dashed" plain @click="exit">
                            {{ hasDone ? step.doneBtnText : step.closeBtnText }}
                        </el-button>
                    </div>

                    <div class="action-step">
                        <el-button v-if="showPrevBtn" class="prev-btn" size="mini" plain @click="previous">
                            {{ step.prevBtnText }}
                        </el-button>
                        <el-button v-if="showNextBtn" class="next-btn" size="mini" plain @click="next">
                            {{ step.nextBtnText }}
                        </el-button>
                    </div>
                </div>
            </div>

            <template v-slot:reference>
                <div v-show="showStage" id="guide-stage" :style="stageStyle"/>
            </template>
        </el-popover>

        <div v-if="showStage" id="guide-highlight-element-cover" :style="stageStyle" @click.prevent.stop/>

        <div v-if="showOverlay" id="guide-overlay"/>
    </div>
</template>

<script>
/**
 * 由driver.js改造而来
 */
import {debounce, isEmpty, deepClone} from "@/util"
import {addHighlightClasses, getCalculatedPosition, removeHighlightClasses} from "./utils"

export default {
    name: "Guide",

    data() {
        return {
            steps: [],
            options: {},
            beforeExit: null,
            isActive: false,
            showStage: false,
            showOverlay: false,
            currentStep: 0,
            moving: false,
            stageStyle: '',
            highlightedElement: null,
            lastHighlightedElement: null
        }
    },

    computed: {
        showPopover() {
            return !this.moving && this.showStage
        },
        showPrevBtn() {
            return this.step.forceShowPrevBtn || this.currentStep > 0 && this.steps[this.currentStep - 1]
        },
        showNextBtn() {
            return this.step.forceShowNextBtn || this.steps[this.currentStep + 1]
        },
        hasDone() {
            return this.currentStep === this.steps.length - 1
        },
        step() {
            if (!this.isActive || this.steps.length <= 0 || !this.steps[this.currentStep]) {
                return {}
            }

            return {
                ...this.options,
                ...this.steps[this.currentStep],
                index: this.currentStep
            }
        }
    },

    watch: {
        isActive(v) {
            document.body.classList[v ? 'add' : 'remove']('guide-body-overflow-hidden')
        }
    },

    methods: {
        start(index = 0) {
            if (!this.steps || this.steps.length === 0) throw new Error('请传入步骤')

            if (index === 'last') index = this.steps.length - 1
            else if (index === 'first') index = 0

            if (index > this.steps.length - 1 || !this.steps[index]) {
                console.error('error index:', index, 'error steps:', deepClone(this.steps))
                throw new Error('起始步骤有误')
            }

            this.isActive = true
            this.currentStep = index
            this.showOverlay = false
            this.showStage = false

            this.$_highlight()

            this.showOverlay = true
            this.showStage = true
        },
        exit(force) {
            if (!this.isActive) return

            if (!force && this.beforeExit) {
                const result = this.beforeExit(this.hasDone)
                if (result && result.then) {
                    result.then(this.$_clear)
                }
                else result !== false && this.$_clear()
                return
            }

            this.$_clear()
        },

        previous() {
            if (!this.isActive || this.moving) return
            this.moving = true

            if (this.step.onPrevious) {
                const result = this.step.onPrevious()
                if (result && result.then) {
                    result.then(this.$_movePrevious, this.$_moveEnd)
                }
                else result ? this.$_movePrevious() : this.$_moveEnd()
            }
            else this.$_movePrevious()
        },
        next() {
            if (!this.isActive || this.moving) return
            this.moving = true

            if (this.step.onNext) {
                const result = this.step.onNext()
                if (result && result.then) {
                    result.then(this.$_moveNext, this.$_moveEnd)
                }
                else result ? this.$_moveNext() : this.$_moveEnd()
            }
            else this.$_moveNext()
        },

        resize() {
            if (!this.highlightedElement) return
            this.$_setStageStyle(getCalculatedPosition(this.highlightedElement))
            this.$nextTick(this.$refs.popover.updatePopper)
        },

        $_movePrevious() {
            if (!this.isActive) return
            this.currentStep--
            this.$_highlight()
        },
        $_moveNext() {
            if (!this.isActive) return
            this.currentStep++
            this.$_highlight()
        },
        $_moveEnd() {
            this.$refs.popover.updatePopper()
            this.moving = false
        },
        $_clear() {
            if (!this.isActive) return
            this.isActive = false
            this.showOverlay = false
            this.showStage = false
            this.currentStep = 0
            this.steps = []
            this.beforeExit = null
            this.highlightedElement && removeHighlightClasses(this.highlightedElement)
            this.highlightedElement = null
            this.lastHighlightedElement = null
        },
        $_highlight() {
            if (isEmpty(this.step.element)) throw new Error(`step${this.currentStep}中element为空`)

            const el = document.querySelector(this.step.element)

            if (!el) throw new Error(`${this.step.element} 没有该元素`)

            if (el === this.highlightedElement) return

            this.highlightedElement && removeHighlightClasses(this.highlightedElement)

            this.lastHighlightedElement = this.highlightedElement
            this.highlightedElement = el

            el.scrollIntoView({block: 'center'})

            this.$_setStageStyle(getCalculatedPosition(el))

            window.setTimeout(() => {
                this.$_moveEnd()
                this.step.onHighlighted && this.step.onHighlighted()
            }, 300)

            addHighlightClasses(el)
        },
        $_setStageStyle({top, left, right, bottom}) {
            const width = right - left
            const height = bottom - top
            this.stageStyle =
                `width:${width}px;` +
                `height:${height}px;` +
                `top:${top}px;` +
                `left:${left}px;` +
                `background-color:${this.step.stageBackground}`
        }
    },

    mounted() {
        this.resize = debounce(this.resize)

        window.addEventListener('resize', this.resize)

        this.$once('hook:beforeDestroy', () => {
            window.removeEventListener('resize', this.resize)
        })
    }
}
</script>

<style lang="scss" src="./style.scss"></style>

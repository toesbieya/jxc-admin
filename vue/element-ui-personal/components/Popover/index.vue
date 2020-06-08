<template>
    <div>
        <slot name="reference"/>
        <transition :name="transition" @after-enter="handleAfterEnter" @after-leave="handleAfterLeave">
            <div
                    ref="popper"
                    v-show="!disabled && showPopper"
                    class="el-popover el-popper"
                    :class="[popperClass, content && 'el-popover--plain']"
                    :style="{ width: width + 'px' }"
                    :id="tooltipId"
            >
                <div class="el-popover__title" v-if="title">
                    <slot name="title">{{title}}</slot>
                </div>
                <slot>{{ content }}</slot>
            </div>
        </transition>
    </div>
</template>

<script>
    import Popper from '@ele/mixins/vue-popper'
    import {on, off} from 'element-ui/lib/utils/dom'
    import {addClass, removeClass} from 'element-ui/lib/utils/dom'
    import {generateId} from 'element-ui/lib/utils/util'

    export default {
        name: "ElPopover",

        mixins: [Popper],

        props: {
            trigger: {
                type: String,
                default: 'click',
                validator: value => ['click', 'focus', 'hover', 'manual'].indexOf(value) > -1
            },
            openDelay: {type: Number, default: 0},
            closeDelay: {type: Number, default: 200},
            transition: {type: String, default: 'fade-in-liner'},
            popperClass: String,
            title: String,
            disabled: Boolean,
            content: String,
            width: {},
            arrowOffset: {type: Number, default: 0}
        },

        data() {
            return {}
        },

        computed: {
            tooltipId() {
                return `el-popover-${generateId()}`
            }
        },

        watch: {
            showPopper(val) {
                if (this.disabled) return
                this.$emit(val ? 'show' : 'hide')
            }
        },

        methods: {
            doToggle() {
                this.showPopper = !this.showPopper
            },

            doShow() {
                this.showPopper = true
            },

            doClose() {
                this.showPopper = false
            },

            handleFocus() {
                addClass(this.referenceElm, 'focusing')
                if (this.trigger === 'click' || this.trigger === 'focus') this.showPopper = true
            },

            handleClick() {
                removeClass(this.referenceElm, 'focusing')
            },

            handleBlur() {
                removeClass(this.referenceElm, 'focusing')
                if (this.trigger === 'click' || this.trigger === 'focus') this.showPopper = false
            },

            handleMouseEnter() {
                window.clearTimeout(this._timer)
                if (this.openDelay) {
                    this._timer = setTimeout(() => this.showPopper = true, this.openDelay)
                }
                else this.showPopper = true
            },

            handleKeydown(ev) {
                if (ev.keyCode === 27 && this.trigger !== 'manual') { // esc
                    this.doClose()
                }
            },

            handleMouseLeave() {
                window.clearTimeout(this._timer)
                if (this.closeDelay) {
                    this._timer = setTimeout(() => this.showPopper = false, this.closeDelay)
                }
                else this.showPopper = false
            },

            handleDocumentClick(e) {
                let reference = this.reference || this.$refs.reference
                const popper = this.popper || this.$refs.popper

                if (!reference && this.$slots.reference && this.$slots.reference[0]) {
                    reference = this.referenceElm = this.$slots.reference[0].elm
                }
                if (!this.$el ||
                    !reference ||
                    this.$el.contains(e.target) ||
                    reference.contains(e.target) ||
                    !popper ||
                    popper.contains(e.target)) return
                this.showPopper = false
            },

            handleAfterEnter() {
                this.$emit('after-enter')
            },

            handleAfterLeave() {
                this.$emit('after-leave')
                this.doDestroy()
            },

            cleanup() {
                if (this.openDelay || this.closeDelay) {
                    window.clearTimeout(this._timer)
                }
            }
        },

        mounted() {
            let reference = this.referenceElm = this.reference || this.$refs.reference
            const popper = this.popper || this.$refs.popper

            if (!reference && this.$slots.reference && this.$slots.reference[0]) {
                reference = this.referenceElm = this.$slots.reference[0].elm
            }

            if (reference) {
                addClass(reference, 'el-popover__reference')

                if (this.trigger !== 'click') {
                    on(reference, 'focusin', () => {
                        this.handleFocus()
                        const instance = reference.__vue__
                        if (instance && typeof instance.focus === 'function') {
                            instance.focus()
                        }
                    })
                    on(popper, 'focusin', this.handleFocus)
                    on(reference, 'focusout', this.handleBlur)
                    on(popper, 'focusout', this.handleBlur)
                }
                on(reference, 'keydown', this.handleKeydown)
                on(reference, 'click', this.handleClick)
            }
            if (this.trigger === 'click') {
                on(reference, 'click', this.doToggle)
                on(document, 'click', this.handleDocumentClick)
            }
            else if (this.trigger === 'hover') {
                on(reference, 'mouseenter', this.handleMouseEnter)
                on(popper, 'mouseenter', this.handleMouseEnter)
                on(reference, 'mouseleave', this.handleMouseLeave)
                on(popper, 'mouseleave', this.handleMouseLeave)
            }
            else if (this.trigger === 'focus') {
                if (reference.querySelector('input, textarea')) {
                    on(reference, 'focusin', this.doShow)
                    on(reference, 'focusout', this.doClose)
                }
                else {
                    on(reference, 'mousedown', this.doShow)
                    on(reference, 'mouseup', this.doClose)
                }
            }
        },

        beforeDestroy() {
            this.cleanup()

            const reference = this.reference

            off(reference, 'click', this.doToggle)
            off(reference, 'mouseup', this.doClose)
            off(reference, 'mousedown', this.doShow)
            off(reference, 'focusin', this.doShow)
            off(reference, 'focusout', this.doClose)
            off(reference, 'mousedown', this.doShow)
            off(reference, 'mouseup', this.doClose)
            off(reference, 'mouseleave', this.handleMouseLeave)
            off(reference, 'mouseenter', this.handleMouseEnter)
            off(document, 'click', this.handleDocumentClick)
        },

        deactivated() {
            this.cleanup()
        },
    }
</script>

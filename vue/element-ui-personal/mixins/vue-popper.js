import {PopupManager} from 'element-ui/lib/utils/popup'

import PopperJS from 'popper.js'
//import * as PopperJS from '@popperjs/core'

const POPPER_VERSION = 'v1'

if (POPPER_VERSION === 'v2') {
    console.warn = () => ({})
}

const stop = e => e.stopPropagation()

const defaultOptions = () => {
    return POPPER_VERSION === 'v2' ? {modifiers: []} : {modifiers: {computeStyle: {gpuAcceleration: false}}}
}

export default {
    props: {
        transformOrigin: {type: [Boolean, String], default: true},
        placement: {type: String, default: 'bottom'},
        boundariesPadding: {type: Number, default: 5},
        reference: {},
        popper: {},
        offset: {type: Number, default: POPPER_VERSION === 'v2' ? 12 : 0},
        value: Boolean,
        visibleArrow: {type: Boolean, default: true},
        arrowOffset: {type: Number, default: 35},
        appendToBody: {type: Boolean, default: true},
        popperOptions: {type: Object, default: defaultOptions}
    },

    data() {
        return {
            showPopper: this.value
        }
    },

    watch: {
        value: {
            immediate: true,
            handler(val) {
                this.showPopper = val
                this.$emit('input', val)
                if (this.disabled) return
                val && this.updatePopper()
            }
        }
    },

    methods: {
        createPopper() {
            if (!/^(top|bottom|left|right)(-start|-end)?$/g.test(this.placement)) {
                return
            }

            const options = this.popperOptions
            const popper = this.popperElm = this.popperElm || this.popper || this.$refs.popper
            let reference = this.referenceElm = this.referenceElm || this.reference || this.$refs.reference

            if (!reference && this.$slots.reference && this.$slots.reference[0]) {
                reference = this.referenceElm = this.$slots.reference[0].elm
            }

            if (!popper || !reference) return

            if (this.popperJS && this.popperJS.hasOwnProperty('destroy')) {
                this.popperJS.destroy()
            }

            if (this.appendToBody) document.body.appendChild(this.popperElm)
            if (this.visibleArrow) this.setArrow()
            this.setOffset(options)

            options.placement = this.placement
            const createEvent = POPPER_VERSION === 'v2' ? 'onFirstUpdate' : 'onCreate'
            options[createEvent] = () => {
                this.$emit('created', this)
                this.resetTransformOrigin()
                this.$nextTick(this.updatePopper)
            }

            this.popperJS = POPPER_VERSION === 'v2' ?
                PopperJS.createPopper(reference, popper, options)
                : new PopperJS(reference, popper, options)

            popper.style.zIndex = PopupManager.nextZIndex()
            this.popperElm.addEventListener('click', stop)
        },

        updatePopper() {
            const popper = this.popperJS

            if (!popper) return this.createPopper()

            popper.update()
            this.popperElm.style.zIndex = PopupManager.nextZIndex()
        },

        doDestroy(forceDestroy) {
            if (!this.popperJS || (this.showPopper && !forceDestroy)) return
            this.popperJS.destroy()
            this.popperJS = null
        },

        destroyPopper() {
            this.popperJS && this.resetTransformOrigin()
        },

        setOffset(options) {
            if (POPPER_VERSION === 'v2') {
                const modifier = this.popperOptions.modifiers.find(modifier => modifier.name === 'offset'),
                    offset = {offset: [0, this.offset]}

                if (!modifier) {
                    this.popperOptions.modifiers.push({name: 'offset', options: offset})
                }
                else modifier.options = offset
            }
            else {
                if (!options.modifiers.offset) {
                    options.modifiers.offset = {}
                }
                options.modifiers.offset.offset = this.offset
            }
        },

        setArrow() {
            if (this.appended) return
            this.appended = true

            const arrow = document.createElement('div')
            arrow.className = 'popper__arrow'
            arrow.setAttribute(POPPER_VERSION === 'v2' ? 'data-popper-arrow' : 'x-arrow', '')
            const attributes = this.popperElm.attributes
            const key = Object.keys(attributes).find(key => /^_v-/.test(attributes[key].name))
            if (key) arrow.setAttribute(attributes[key].name, '')

            this.popperElm.appendChild(arrow)
        },

        resetTransformOrigin() {
            if (!this.transformOrigin) return
            const placementMap = {top: 'bottom', bottom: 'top', left: 'right', right: 'left'}
            const attr = POPPER_VERSION === 'v2' ? 'data-popper-placement' : 'x-placement'
            const placement = this.popperElm.getAttribute(attr).split('-')[0]
            const origin = placementMap[placement]
            this.popperElm.style.transformOrigin =
                typeof this.transformOrigin === 'string'
                    ? this.transformOrigin
                    : ['top', 'bottom'].includes(placement) ? `center ${origin}` : `${origin} center`
        }
    },

    deactivated() {
        this.$options.beforeDestroy[0].call(this)
    },

    beforeDestroy() {
        this.doDestroy(true)
        if (this.popperElm && this.popperElm.parentNode === document.body) {
            this.popperElm.removeEventListener('click', stop)
            document.body.removeChild(this.popperElm)
        }
    }
}

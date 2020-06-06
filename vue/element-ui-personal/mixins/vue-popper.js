import {PopupManager} from 'element-ui/lib/utils/popup'

const PopperJS = require('@popperjs/core')

const stop = e => e.stopPropagation()

/**
 * @param {HTMLElement} [reference=$refs.reference] - The reference element used to position the popper.
 * @param {HTMLElement} [popper=$refs.popper] - The HTML element used as popper, or a configuration used to generate the popper.
 * @param {String} [placement=button] - Placement of the popper accepted values: top(-start, -end), right(-start, -end), bottom(-start, -end), left(-start, -end)
 * @param {Number} [offset=0] - Amount of pixels the popper will be shifted (can be negative).
 * @param {Boolean} [visible=false] Visibility of the popup element.
 * @param {Boolean} [visible-arrow=false] Visibility of the arrow, no style.
 */
export default {
    props: {
        transformOrigin: {
            type: [Boolean, String],
            default: true
        },
        placement: {
            type: String,
            default: 'bottom'
        },
        boundariesPadding: {
            type: Number,
            default: 5
        },
        reference: {},
        popper: {},
        offset: {
            type: Number,
            default: 12
        },
        value: Boolean,
        visibleArrow: {
            type: Boolean,
            default: true
        },
        arrowOffset: {
            type: Number,
            default: 35
        },
        appendToBody: Boolean,
        popperOptions: {
            type: Object,
            default: () => ({modifiers: []})
        }
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
                if (this.disabled) return
                val && this.updatePopper()
                this.showPopper = val
                this.$emit('input', val)
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
            this.setOffset()

            options.placement = this.placement
            options.onFirstUpdate = () => {
                this.$emit('created', this)
                this.$nextTick(this.updatePopper)
            }


            this.popperJS = PopperJS.createPopper(reference, popper, options)

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

        setArrow() {
            if (this.appended) return
            this.appended = true

            const arrow = document.createElement('div')
            arrow.className = 'popper__arrow'
            arrow.setAttribute('data-popper-arrow', '')

            this.popperElm.appendChild(arrow)

            const modifier = this.popperOptions.modifiers.find(modifier => modifier.name === 'arrow'),
                options = {element: '[data-popper-arrow]', padding: this.arrowOffset}

            if (!modifier) {
                this.popperOptions.modifiers.push({name: 'arrow', options})
            }
            else modifier.options = options
        },

        setOffset() {
            const modifier = this.popperOptions.modifiers.find(modifier => modifier.name === 'offset'),
                options = {offset: [0, this.offset]}

            if (!modifier) {
                this.popperOptions.modifiers.push({name: 'offset', options})
            }
            else modifier.options = options
        }
    },

    beforeDestroy() {
        this.doDestroy(true)
        if (this.popperElm && this.popperElm.parentNode === document.body) {
            this.popperElm.removeEventListener('click', stop)
            document.body.removeChild(this.popperElm)
        }
    },

    // call destroy in keep-alive mode
    deactivated() {
        this.$options.beforeDestroy[0].call(this)
    }
}

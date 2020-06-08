<template>
    <div
            class="el-select-dropdown el-popper"
            :class="[{ 'is-multiple': $parent.multiple }, popperClass]"
            :style="{ minWidth }"
    >
        <slot/>
    </div>
</template>

<script type="text/babel">
    import Popper from '@ele/mixins/vue-popper'

    export default {
        name: 'ElSelectDropdown',

        componentName: 'ElSelectDropdown',

        mixins: [Popper],

        data() {
            return {
                minWidth: ''
            }
        },

        computed: {
            popperClass() {
                return this.$parent.popperClass
            }
        },

        watch: {
            '$parent.inputWidth'() {
                this.minWidth = this.$parent.$el.getBoundingClientRect().width + 'px'
            }
        },

        mounted() {
            this.referenceElm = this.$parent.$refs.reference.$el
            this.$parent.popperElm = this.popperElm = this.$el
            this.$on('updatePopper', () => {
                this.$parent.visible && this.updatePopper()
            })
            this.$on('destroyPopper', this.destroyPopper)
        }
    }
</script>

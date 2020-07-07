<template>
    <li
            class="el-menu-item"
            :style="paddingStyle"
            :class="{'is-active': active,'is-disabled': disabled}"
            @click="handleClick"
    >
        <el-tooltip
                v-if="parentMenu.$options.componentName === 'ElMenu' && rootMenu.collapse && $slots.title"
                effect="dark"
                placement="right"
        >
            <div slot="content">
                <slot name="title"/>
            </div>
            <div style="position: absolute;left: 0;top: 0;height: 100%;width: 100%;display: inline-block;box-sizing: border-box;padding: 0 20px;">
                <slot/>
            </div>
        </el-tooltip>

        <template v-else>
            <slot/>
            <slot name="title"/>
        </template>
    </li>
</template>

<script>
    import MenuMixin from 'element-ui/packages/menu/src/menu-mixin'
    import Emitter from 'element-ui/lib/mixins/emitter'

    export default {
        name: 'ElMenuItem',

        componentName: 'ElMenuItem',

        mixins: [MenuMixin, Emitter],

        data() {
            return {}
        },

        props: {
            index: String,
            route: [String, Object],
            disabled: Boolean
        },

        computed: {
            active() {
                return this.index === this.rootMenu.activeIndex
            }
        },

        methods: {
            handleClick() {
                if (!this.disabled) {
                    this.dispatch('ElMenu', 'item-click', this)
                    this.$emit('click', this)
                }
            }
        },

        mounted() {
            this.parentMenu.addItem(this)
            this.rootMenu.addItem(this)
        },

        beforeDestroy() {
            this.parentMenu.removeItem(this)
            this.rootMenu.removeItem(this)
        }
    }
</script>

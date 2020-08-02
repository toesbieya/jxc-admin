<script type="text/jsx">
import MenuMixin from 'element-ui/packages/menu/src/menu-mixin'
import Emitter from 'element-ui/lib/mixins/emitter'
import Popper from 'element-ui/lib/utils/vue-popper'

const PopperMixins = {
    props: {
        transformOrigin: {type: [Boolean, String], default: false},
        offset: Popper.props.offset,
        boundariesPadding: Popper.props.boundariesPadding,
        popperOptions: Popper.props.popperOptions
    },
    data: Popper.data,
    methods: Popper.methods,
    beforeDestroy: Popper.beforeDestroy,
    deactivated: Popper.deactivated
}

export default {
    name: 'ElSubmenu',

    componentName: 'ElSubmenu',

    mixins: [MenuMixin, Emitter, PopperMixins],

    props: {
        index: String,
        showTimeout: {type: Number, default: 300},
        hideTimeout: {type: Number, default: 300},
        popperClass: String,
        disabled: Boolean,
        popperAppendToBody: {type: Boolean, default: true}
    },

    data() {
        return {
            popperJS: null,
            timeout: null,
            items: {},
            submenus: {},
            mouseInChild: false
        }
    },

    computed: {
        appendToBody() {
            return this.popperAppendToBody === undefined
                ? this.isFirstLevel
                : this.popperAppendToBody
        },
        menuTransitionName() {
            return this.rootMenu.collapse ? 'el-zoom-in-left' : 'el-zoom-in-top'
        },
        opened() {
            return this.rootMenu.openedMenus.includes(this.index)
        },
        active() {
            const items = this.items

            const isActive = Object.keys(items).some(key => items[key].active)

            if (!isActive) {
                const submenus = this.submenus
                return Object.keys(submenus).some(key => submenus[key].active)
            }

            return isActive
        },
        mode() {
            return this.rootMenu.mode
        },
        isMenuPopup() {
            return this.rootMenu.isMenuPopup
        },
        isFirstLevel() {
            const arr = ['ElSubmenu', 'ElMenuItemGroup']
            let isFirstLevel = true
            let parent = this.$parent

            while (parent && parent !== this.rootMenu) {
                if (arr.includes(parent.$options.componentName)) {
                    isFirstLevel = false
                    break
                }
                else parent = parent.$parent
            }
            return isFirstLevel
        }
    },

    watch: {
        opened() {
            this.isMenuPopup && this.$nextTick(this.updatePopper)
        }
    },

    methods: {
        handleCollapseToggle(value) {
            if (value) {
                this.initPopper()
            }
            else {
                this.doDestroy()
            }
        },

        addItem(item) {
            this.$set(this.items, item.index, item)
        },

        removeItem(item) {
            delete this.items[item.index]
        },

        addSubmenu(item) {
            this.$set(this.submenus, item.index, item)
        },

        removeSubmenu(item) {
            delete this.submenus[item.index]
        },

        handleClick() {
            const {rootMenu, disabled} = this
            if (
                (rootMenu.menuTrigger === 'hover' && rootMenu.mode === 'horizontal') ||
                (rootMenu.collapse && rootMenu.mode === 'vertical') ||
                disabled
            ) {
                return
            }
            this.dispatch('ElMenu', 'submenu-click', this)
        },

        handleMouseenter(event, showTimeout = this.showTimeout) {

            if (!('ActiveXObject' in window) && event.type === 'focus' && !event.relatedTarget) {
                return
            }
            const {rootMenu, disabled} = this
            if (
                (rootMenu.menuTrigger === 'click' && rootMenu.mode === 'horizontal') ||
                (!rootMenu.collapse && rootMenu.mode === 'vertical') ||
                disabled
            ) {
                return
            }
            this.dispatch('ElSubmenu', 'mouse-enter-child')
            clearTimeout(this.timeout)
            this.timeout = setTimeout(() => {
                this.rootMenu.openMenu(this.index, this.indexPath)
            }, showTimeout)

            if (this.appendToBody) {
                this.$parent.$el.dispatchEvent(new MouseEvent('mouseenter'))
            }
        },

        handleMouseleave(deepDispatch = false) {
            const {rootMenu} = this
            if (
                (rootMenu.menuTrigger === 'click' && rootMenu.mode === 'horizontal') ||
                (!rootMenu.collapse && rootMenu.mode === 'vertical')
            ) {
                return
            }
            this.dispatch('ElSubmenu', 'mouse-leave-child')
            clearTimeout(this.timeout)
            this.timeout = setTimeout(() => {
                !this.mouseInChild && this.rootMenu.closeMenu(this.index)
            }, this.hideTimeout)

            if (this.appendToBody && deepDispatch) {
                if (this.$parent.$options.name === 'ElSubmenu') {
                    this.$parent.handleMouseleave(true)
                }
            }
        },

        updatePlacement() {
            this.placement = this.mode === 'horizontal' && this.isFirstLevel
                ? 'bottom-start'
                : 'right-start'
        },

        initPopper() {
            this.referenceElm = this.$el
            this.popperElm = this.$refs.menu
            this.updatePlacement()
        }
    },

    created() {
        this.$on('toggle-collapse', this.handleCollapseToggle)
        this.$on('mouse-enter-child', () => {
            this.mouseInChild = true
            clearTimeout(this.timeout)
        })
        this.$on('mouse-leave-child', () => {
            this.mouseInChild = false
            clearTimeout(this.timeout)
        })
    },

    mounted() {
        this.parentMenu.addSubmenu(this)
        this.rootMenu.addSubmenu(this)
        this.initPopper()
    },

    beforeDestroy() {
        this.parentMenu.removeSubmenu(this)
        this.rootMenu.removeSubmenu(this)
    },

    render() {
        const {rootMenu, $slots} = this

        const popupMenu = (
            <transition name={this.menuTransitionName}>
                <div
                    ref="menu"
                    v-show={this.opened}
                    class={[`el-menu--${this.mode}`, this.popperClass]}
                    on-mouseenter={e => this.handleMouseenter(e, 100)}
                    on-mouseleave={() => this.handleMouseleave(true)}
                    on-focus={e => this.handleMouseenter(e, 100)}>
                    <ul class={['el-menu el-menu--popup', `el-menu--popup-${this.currentPlacement}`]}>
                        {$slots.default}
                    </ul>
                </div>
            </transition>
        )

        const inlineMenu = (
            <el-collapse-transition>
                <ul v-show={this.opened} class="el-menu el-menu--inline">
                    {$slots.default}
                </ul>
            </el-collapse-transition>
        )

        const submenuTitleIcon = (
            rootMenu.mode === 'horizontal' && this.isFirstLevel ||
            rootMenu.mode === 'vertical' && !rootMenu.collapse
        ) ? 'el-icon-arrow-down' : 'el-icon-arrow-right'

        return (
            <li
                class={{
                    'el-submenu': true,
                    'is-active': this.active,
                    'is-opened': this.opened,
                    'is-disabled': this.disabled
                }}
                on-mouseenter={this.handleMouseenter}
                on-mouseleave={() => this.handleMouseleave(false)}
                on-focus={this.handleMouseenter}
            >
                <div
                    ref="submenu-title"
                    class="el-submenu__title"
                    style={this.paddingStyle}
                    on-click={this.handleClick}
                >
                    {$slots.title}
                    <i class={['el-submenu__icon-arrow', submenuTitleIcon]}/>
                </div>
                {this.isMenuPopup ? popupMenu : inlineMenu}
            </li>
        )
    }
}
</script>

<style>

</style>

<script type="text/jsx">
/**
 * 简易顶部菜单，参考了ant design的响应式设计
 */
import ClickOutside from 'element-ui/lib/utils/clickoutside'
import actionOnSelectMenuMixin from "@/layout/mixin/actionOnSelectMenu"

export default {
    name: "RootMenu",

    mixins: [actionOnSelectMenuMixin],

    directives: {ClickOutside},

    props: {
        //当前激活菜单的路径
        activeMenu: String,
        //树形菜单数组
        menu: Array,
        //是否在顶部菜单个数<=1时仍然渲染
        alwaysShow: Boolean
    },

    data() {
        return {
            //最后一个不被隐藏的顶部菜单的数组下标
            //为undefined时，说明不需要隐藏菜单
            //为-1时，说明需要隐藏全部菜单
            lastVisibleIndex: undefined,

            //是否显示menu-popover
            showPopover: false
        }
    },

    methods: {
        onSelect(menu, e) {
            e.stopPropagation()
            const path = menu.fullPath
            if (path === this.activeMenu) return
            this.showPopover = false
            this.actionOnSelectMenu(path, false)
        },

        //获取初始菜单的总宽度，只在mounted时调用一次
        setChildrenWidth() {
            const ul = this.$el
            if (!ul) return
            const menuItemNodes = ul.children
            if (!menuItemNodes || menuItemNodes.length === 0) return
            //'更多'菜单的宽度，由于不考虑自定义，所以直接写死
            this.overflowedIndicatorWidth = 57
            this.menuItemSizes = Array.from(menuItemNodes).map(i => i.getBoundingClientRect().width)
            this.originalTotalWidth = this.menuItemSizes.reduce((acc, cur) => acc + cur, 0)
        },

        resize() {
            const width = this.$el.getBoundingClientRect().width

            let lastVisibleIndex = undefined

            //如果初始菜单的总宽度超出容器宽度
            if (this.originalTotalWidth > width) {
                lastVisibleIndex = -1

                //得到满足总宽度不超出容器宽度的最大菜单下标
                let currentSumWidth = 0
                for (const liWidth of this.menuItemSizes) {
                    currentSumWidth += liWidth
                    if (currentSumWidth + this.overflowedIndicatorWidth > width) {
                        break
                    }
                    lastVisibleIndex += 1
                }
            }

            this.lastVisibleIndex = lastVisibleIndex
        },

        renderItemContent(h, menu) {
            const {title, icon} = menu.meta
            const arr = []
            icon && arr.push(<v-icon icon={icon}/>)
            arr.push(title)
            return arr
        },

        renderOverflowedIndicator(h, hideAll) {
            return (
                <div class="root-menu-item-title">
                    {hideAll ? <i class="el-icon-menu"/> : '...'}
                </div>
            )
        },

        renderVisibleMenus(h, visibleMenus) {
            if (visibleMenus.length <= 0) return
            const activeMenu = this.activeMenu
            return visibleMenus.map(i => {
                const key = i.fullPath + i.meta.title
                const className = {'root-menu-item': true, 'active': i.fullPath === activeMenu}
                return (
                    <li key={key} class={className} on-click={e => this.onSelect(i, e)}>
                        <div class="root-menu-item-title">
                            {this.renderItemContent(h, i)}
                        </div>
                    </li>
                )
            })
        },

        renderHideMenus(h, hideMenus) {
            if (hideMenus.length <= 0) return
            const activeMenu = this.activeMenu
            let hasActiveChild = false
            const children = hideMenus.map(i => {
                const className = {'root-sub-menu-item': true, 'active': i.fullPath === activeMenu}
                if (className.active) hasActiveChild = true
                return (
                    <li class={className} on-click={e => this.onSelect(i, e)}>
                        {this.renderItemContent(h, i)}
                    </li>
                )
            })
            const className = {
                'root-menu-item': true,
                'root-sub-menu': true,
                'active': this.showPopover || hasActiveChild
            }
            return (
                <li
                    v-click-outside={() => this.showPopover = false}
                    key="overflowed-indicator"
                    class={className}
                    on-click={() => this.showPopover = true}
                >
                    {this.renderOverflowedIndicator(h, hideMenus.length === this.menu.length)}
                    <transition name="el-zoom-in-top">
                        <ul v-show={this.showPopover} class="menu-popover">
                            {children}
                        </ul>
                    </transition>
                </li>
            )
        }
    },

    mounted() {
        this.setChildrenWidth()

        this.resizeObserver = new ResizeObserver(() => this.resize())
        this.resizeObserver.observe(this.$el)

        this.$once('hook:beforeDestroy', () => {
            this.resizeObserver && this.resizeObserver.disconnect()
        })
    },

    render(h) {
        const {lastVisibleIndex, menu, alwaysShow} = this
        if (!Array.isArray(menu) || menu.length <= 1 && !alwaysShow) {
            return
        }

        let visibleMenus = menu, hideMenus = []
        if (lastVisibleIndex !== undefined) {
            //不需要隐藏菜单
            if (lastVisibleIndex === -1) {
                visibleMenus = []
                hideMenus = menu
            }
            //需要隐藏全部菜单
            else if (lastVisibleIndex !== menu.length - 1) {
                visibleMenus = menu.slice(0, lastVisibleIndex + 1)
                hideMenus = menu.slice(lastVisibleIndex + 1)
            }
        }

        return (
            <ul class="root-menu">
                {this.renderVisibleMenus(h, visibleMenus)}
                {this.renderHideMenus(h, hideMenus)}
            </ul>
        )
    }
}
</script>

<style lang="scss">
@import "~@/asset/style/variables.scss";

.root-menu {
    width: 100%;
    flex: 1;
    height: $nav-height;
    line-height: $nav-height;
    list-style: none;
    margin: 0;
    padding: 0;
    font-size: 14px;
    color: #515a6e;

    &-item {
        height: inherit;
        border-bottom: 2px solid transparent;
        float: left;
        padding: 0 20px;
        cursor: pointer;
        transition: all .2s ease-in-out;

        &:hover, &.active {
            border-bottom: 2px solid $--color-primary;

            > .root-menu-item-title {
                color: $--color-primary;
            }
        }
    }

    .root-sub-menu {
        position: relative;

        > .menu-popover {
            position: absolute;
            left: -20px;
            background-color: #fff;
            margin: 5px 0;
            padding: 5px 0;
            min-width: 100px;
            text-align: left;
            border-radius: 4px;
            box-shadow: 0 1px 6px rgba(0, 0, 0, .2);
            z-index: 10;
        }

        &-item {
            margin: 0;
            line-height: normal;
            padding: 7px 16px;
            color: #515a6e;
            white-space: nowrap;
            list-style: none;
            cursor: pointer;

            &.active, &:hover {
                color: $--color-primary;
            }
        }
    }

    .root-menu-item-title, .root-sub-menu-item {
        > .icon {
            font-size: 14px;
            margin-right: 6px;
            vertical-align: -0.125em;
        }
    }
}
</style>

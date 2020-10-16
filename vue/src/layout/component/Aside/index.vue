<script type="text/jsx">
import menuMixin from "@/layout/mixin/menu"
import {getters as appGetters} from "@/layout/store/app"
import {getters as asideGetters, mutations as asideMutations} from "@/layout/store/aside"
import Logo from './Logo'
import NavMenuItem from '@/component/menu/NavMenu/item'
import {getSidebarMenus, getActiveMenuByRoute} from "@/layout/util"
import {getTopDistance} from "@/util/browser"

export default {
    name: 'Aside',

    mixins: [menuMixin],

    components: {Logo, NavMenuItem},

    data() {
        return {
            //鼠标是否在侧边栏外
            mouseOutside: true
        }
    },

    computed: {
        isMobile: () => appGetters.isMobile,
        activeRootMenu: () => appGetters.activeRootMenu,

        //侧边栏菜单
        menus: () => getSidebarMenus(),

        //当时移动端或设置了侧边栏自动隐藏时将侧边栏用抽屉包裹
        renderInDrawer() {
            return this.isMobile || asideGetters.autoHide
        },

        //侧边栏的显隐状态，true显示、false隐藏
        show() {
            //store中要显示那就显示
            if (asideGetters.show) return true

            //移动端，false
            if (this.isMobile) return false

            //未设置侧边栏自动隐藏，false
            if (!asideGetters.autoHide) return false

            //鼠标在侧边栏内，true
            if (!this.mouseOutside) return true

            //侧边栏处于折叠状态 且 存在弹出的子菜单，true
            return this.collapse && this.openedMenus.length > 0
        },

        //侧边栏的折叠状态，true折叠、false展开，仅在pc端可折叠
        collapse() {
            return asideGetters.collapse && !this.isMobile
        },

        //判断是否需要绑定鼠标移动的事件
        shouldBindMouseMoveEvent() {
            //如果是移动端，false
            if (this.isMobile) return false

            //如果未启用侧边栏自动隐藏，false
            if (!asideGetters.autoHide) return false

            //侧边栏为打开状态，false
            if (this.show) return false

            //鼠标在侧边栏外部，true
            return this.mouseOutside
        }
    },

    watch: {
        '$route.path': {
            immediate: true,
            handler(v) {
                //如果是redirect跳转，则跳过
                if (v.startsWith('/redirect')) return

                this.activeMenu = getActiveMenuByRoute(this.$route)

                const menu = this.$refs.menu
                if (!menu) return
                const item = menu.items[this.activeMenu]

                //如果侧边栏中没有对应的激活菜单，则收起全部
                if (!item) return menu.openedMenus = []

                //由于elMenu的initOpenedMenu()不会触发select事件，所以选择手动触发
                this.onSelect(item.index, item.indexPath, item, false)

                //仅当非抽屉模式下滚动至激活的菜单
                !this.renderInDrawer && this.$nextTick(this.moveToCurrentMenu)
            }
        },

        //切换至移动端时收起侧边栏
        isMobile: {
            immediate: true,
            handler(v) {
                v && asideMutations.close()
            }
        },

        //顶部菜单改变时重设高亮项（ele考虑不周，只要菜单被点击就会激活）
        activeRootMenu(v) {
            v && this.resetActiveMenu()
        },

        //抽屉模式下侧边栏显示时滚动至激活菜单
        show(v) {
            v && this.$nextTick(this.moveToCurrentMenu)
        },

        //添加或移除鼠标移动事件
        shouldBindMouseMoveEvent: {
            immediate: true,
            handler(v) {
                //尝试移除之前可能添加的事件
                window.removeEventListener('mousemove', this.moveEvent)

                v && window.addEventListener('mousemove', this.moveEvent)
            }
        }
    },

    methods: {
        //开启侧边栏自动隐藏后的鼠标移动事件
        moveEvent(e) {
            //鼠标移动至屏幕左侧边缘时，标识鼠标在侧边栏内部
            if (e.clientX <= 1) this.mouseOutside = false
        },

        //模拟选中菜单
        onSelect(index, indexPath, item, jump = true) {
            //开启手风琴模式时，激活没有子级的菜单时收起其它展开项
            if (asideGetters.uniqueOpen && indexPath.length === 1) {
                const menu = this.$refs.menu
                const opened = menu.openedMenus
                opened.forEach(i => i !== index && menu.closeMenu(i))
            }

            jump && this.actionOnSelectMenu(index)

            //抽屉模式下需要关闭抽屉
            if (this.renderInDrawer && this.show) {
                asideMutations.close()
                this.mouseOutside = true
            }
        },

        //滚动至当前激活的菜单
        moveToCurrentMenu() {
            const menu = this.$refs.menu, cur = this.activeMenu
            if (!menu || !cur) return

            const curInstance = menu.items[cur]
            if (!curInstance) return

            let el = curInstance.$el

            //当侧边栏折叠时，需要滚动至可视区域的元素是激活菜单的最顶层父节点
            if (this.collapse) {
                let rootParent = curInstance
                while (rootParent.$parent.$options.componentName !== 'ElMenu') {
                    rootParent = rootParent.$parent
                }
                el = rootParent.$el
            }

            /*
            * 这里考虑了菜单展开时的200ms动画时间
            * 为什么不分情况讨论？比如当subMenu已经是展开状态时，无需延时滚动
            * 但这种情况无法判断，因为这时menu.openedMenus已经包含了subMenu，无论subMenu之前是否展开
            * 所以统一延时300ms
            * */
            window.setTimeout(() => this.scrollMenuIntoView(el, menu.$el), 300)
        },

        //将指定菜单滚动到可视区域内
        scrollMenuIntoView(el, container) {
            const {scrollTop, scrollHeight, offsetHeight: menuHeight} = container

            //当菜单高度不足以滚动时跳过
            if (scrollHeight <= menuHeight) return

            const elHeight = el.offsetHeight, between = getTopDistance(el, container)

            //计算需要滚动的距离，undefined说明不需要滚动
            let distance

            if (between < 0) distance = between
            else if (between + elHeight > menuHeight) {
                distance = between + elHeight - menuHeight
            }

            if (distance !== undefined) {
                container.scrollTo({top: scrollTop + distance, behavior: 'smooth'})
            }
        }
    },

    beforeDestroy() {
        window.removeEventListener('mousemove', this.moveEvent)
    },

    render() {
        if (this.menus.length <= 0) return

        const aside = (
            <aside
                class={{'aside': true, 'collapse': this.collapse}}
                on-mouseleave={() => this.mouseOutside = true}
                on-mouseenter={() => this.mouseOutside = false}
            >
                {asideGetters.showLogo && <logo collapse={this.collapse}/>}
                <el-menu
                    ref="menu"
                    class="el-menu--vertical"
                    collapse={this.collapse}
                    collapse-transition={false}
                    default-active={this.activeMenu}
                    unique-opened={asideGetters.uniqueOpen}
                    on={{'select': this.onSelect, 'hook:mounted': this.watchOpenedMenus}}
                >
                    {this.menus.map(m => (
                        <nav-menu-item
                            menu={m}
                            show-parent={asideGetters.showParentOnCollapse}
                            collapse={asideGetters.collapse}
                            show-icon-max-depth={1}
                        />
                    ))}
                </el-menu>
            </aside>
        )

        if (this.renderInDrawer) {
            return (
                <el-drawer
                    visible={this.show}
                    with-header={false}
                    direction="ltr"
                    size="auto"
                    on-close={() => asideMutations.close()}
                >
                    {aside}
                </el-drawer>
            )
        }

        return aside
    }
}
</script>

<style lang="scss" src="./style.scss"></style>

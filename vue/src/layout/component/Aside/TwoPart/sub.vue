<script type="text/jsx">
import menuMixin from "@/layout/mixin/menu"
import {getters as appGetters} from "@/layout/store/app"
import {getters as asideGetters} from "@/layout/store/aside"
import NavMenu from "@/component/menu/NavMenu"
import {getActiveMenuByRoute, getSidebarMenus} from "@/layout/util"
import {moveToActiveMenuVertically} from "@/util/element-ui/elMenu"

export default {
    name: "SubSidebar",

    mixins: [menuMixin],

    components: {NavMenu},

    computed: {
        //父级菜单
        rootMenu() {
            const active = appGetters.activeRootMenu
            return appGetters.menus.find(i => i.fullPath === active)
        },

        //侧边栏菜单
        menus: () => getSidebarMenus(),

        //侧边栏的折叠状态，true折叠、false展开，仅在pc端可折叠
        collapse() {
            return asideGetters.collapse && !asideGetters.isMobile
        },
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

                //滚动至激活的菜单
                this.$nextTick(this.moveToCurrentMenu)
            }
        }
    },

    methods: {
        //模拟选中菜单
        onSelect(index, indexPath, item, jump = true) {
            //开启手风琴模式时，激活没有子级的菜单时收起其它展开项
            if (asideGetters.uniqueOpen && indexPath.length === 1) {
                const menu = this.$refs.menu
                const opened = menu.openedMenus
                opened.forEach(i => i !== index && menu.closeMenu(i))
            }

            jump && this.actionOnSelectMenu(index)
        },

        //滚动至当前激活的菜单
        moveToCurrentMenu() {
            moveToActiveMenuVertically(this.$refs.menu)
        }
    },

    render() {
        if (this.menus.length <= 0) return

        return (
            <div class={{'sub-sidebar': true, 'collapse': this.collapse}}>
                <div class="sub-sidebar-title">
                    {this.rootMenu && this.rootMenu.meta.title}
                </div>
                <nav-menu
                    menus={this.menus}
                    collapse={this.collapse}
                    default-active={this.activeMenu}
                    unique-opened={asideGetters.uniqueOpen}
                    show-parent={asideGetters.showParentOnCollapse}
                    switch-transition
                    on-select={this.onSelect}
                />
            </div>
        )
    }
}
</script>

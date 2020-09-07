<script type="text/jsx">
import menuMixin from "@/layout/mixin/menu"
import {getters as appGetters} from "@/layout/store/app"
import {getters as asideGetters, mutations as asideMutations} from "@/layout/store/aside"
import Logo from './Logo'
import NavMenuItem from '@/component/menu/NavMenu/item'
import {getSidebarMenus, getActiveMenuByRoute} from "@/layout/util"

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
        device: () => appGetters.device,
        activeRootMenu: () => appGetters.activeRootMenu,

        //侧边栏的折叠状态，true折叠false展开，仅在pc端可折叠
        collapse() {
            return asideGetters.collapse && this.device === 'pc'
        },

        //是否隐藏侧边栏
        //①设置了侧边栏自动隐藏且鼠标不在侧边栏内且是pc端
        //②侧边栏处于折叠状态且是移动端
        hide() {
            return asideGetters.autoHide && this.mouseOutside && this.device === 'pc'
                || asideGetters.collapse && this.device === 'mobile'
        }
    },

    watch: {
        //由于elMenu的initOpenedMenu()不会触发select事件，所以手动实现菜单收起
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

                this.onSelect(item.index, item.indexPath, item, false)
            }
        },

        //切换至移动端时收起侧边栏
        device: {
            immediate: true,
            handler(v) {
                v === 'mobile' && asideMutations.close()
            }
        },

        //顶部菜单改变时重设高亮项
        activeRootMenu(v) {
            v && this.resetActiveMenu()
        },

        //设置了侧边栏自动隐藏后，根据状态添加或移除鼠标移动事件
        hide(v) {
            if (!asideGetters.autoHide) return
            const method = `${v ? 'add' : 'remove'}EventListener`
            document[method]('mousemove', this.moveEvent)
        }
    },

    methods: {
        moveEvent(e) {
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
        }
    },

    mounted() {
        if (asideGetters.autoHide) {
            document.addEventListener('mousemove', this.moveEvent)
        }

        this.$once('hook:beforeDestroy', () => {
            document.removeEventListener('mousemove', this.moveEvent)
        })
    },

    render() {
        const menus = getSidebarMenus()
        if (menus.length <= 0) return

        const aside = (
            <aside
                class={{'aside': true, 'collapse': this.collapse}}
                on-mouseenter={() => this.mouseOutside = false}
                on-mouseleave={() => this.mouseOutside = true}
            >
                {asideGetters.showLogo && <logo collapse={this.collapse}/>}
                <el-menu
                    ref="menu"
                    class="el-menu--vertical"
                    collapse={this.collapse}
                    collapse-transition={false}
                    default-active={this.activeMenu}
                    unique-opened={asideGetters.uniqueOpen}
                    on-select={this.onSelect}
                >
                    {menus.map(m => (
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

        //当是移动端 或 设置了侧边栏自动隐藏时，渲染成抽屉
        const renderInDrawer = this.device === 'mobile' || asideGetters.autoHide

        if (renderInDrawer) {
            return (
                <el-drawer
                    visible={asideGetters.show}
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

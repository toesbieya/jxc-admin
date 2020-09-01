<script type="text/jsx">
import actionOnSelectMenuMixin from "@/layout/mixin/actionOnSelectMenu"
import {getters as mainGetters} from "@/layout/store/main"
import {getters as settingGetters, mutations as settingMutations} from "@/layout/store/setting"
import Logo from './component/Logo'
import SidebarItem from './component/SidebarItem'

export default {
    name: 'Aside',

    mixins: [actionOnSelectMenuMixin],

    components: {SidebarItem, Logo},

    data() {
        return {
            //鼠标是否在侧边栏内
            mouseOutside: true,
            //当前激活的菜单的fullPath
            activeMenu: '',
            //侧边栏菜单数组，即当前激活的顶部菜单的children
            sidebarMenus: []
        }
    },

    computed: {
        device: () => mainGetters.device,
        activeRootMenu: () => mainGetters.activeRootMenu,
        menus: () => mainGetters.menus,

        showLogo: () => settingGetters.showLogo,
        sidebarCollapse: () => settingGetters.sidebarCollapse,
        sidebarUniqueOpen: () => settingGetters.sidebarUniqueOpen,
        sidebarShowParent: () => settingGetters.sidebarShowParent,
        sidebarAutoHidden: () => settingGetters.sidebarAutoHidden,

        //侧边栏的折叠状态，true折叠false展开，仅在pc端可折叠
        collapse() {
            return this.sidebarCollapse && this.device === 'pc'
        },

        //是否隐藏侧边栏
        //①设置了侧边栏自动隐藏且鼠标不再侧边栏内且是pc端
        //②侧边栏处于折叠状态且是移动端
        hideSidebar() {
            return this.sidebarAutoHidden && this.mouseOutside && this.device === 'pc'
                || this.sidebarCollapse && this.device === 'mobile'
        },

        sidebarClass() {
            return {
                'sidebar-container': true,
                'mobile': this.device === 'mobile',
                'collapse-sidebar': this.collapse,
                'hide-sidebar': this.hideSidebar
            }
        }
    },

    watch: {
        //由于elMenu的initOpenedMenu()不会触发select事件，所以手动实现菜单收起
        '$route.path': {
            immediate: true,
            handler(v) {
                //如果是redirect跳转，则跳过
                if (v.startsWith('/redirect')) return

                this.setActiveMenu()

                const menu = this.$refs.menu
                if (!menu) return
                const item = menu.items[this.activeMenu]

                //如果侧边栏中没有对应的激活菜单，则收起全部
                if (!item) return menu.openedMenus = []

                this.select(item.index, item.indexPath, item, false)
            }
        },

        activeRootMenu: {
            immediate: true,
            handler(v) {
                const {menus} = this
                if (menus.length <= 0 || !v) return
                const root = menus.find(i => i.path === v)
                this.sidebarMenus = root ? root.children || [] : []

                //由于侧边栏菜单数组更新后，el-menu不一定会更新（当数组中不存在单级菜单时）
                //所以手动更新el-menu的当前高亮菜单
                const menu = this.$refs.menu
                menu && menu.updateActiveIndex()
            }
        },

        //设置了侧边栏自动隐藏后，根据状态添加或移除鼠标移动事件
        hideSidebar: {
            immediate: true,
            handler(v) {
                if (!this.sidebarAutoHidden) return
                const method = `${v ? 'add' : 'remove'}EventListener`
                document[method]('mousemove', this.moveEvent)
            }
        }
    },

    methods: {
        moveEvent(e) {
            if (e.clientX <= 1) this.mouseOutside = false
        },

        //根据路由地址设置当前激活的菜单路径
        setActiveMenu() {
            const {meta, path} = this.$route
            this.activeMenu = meta.activeMenu || path
        },

        //模拟选中菜单
        select(index, indexPath, item, jump = true) {
            //开启手风琴模式时，激活没有子级的菜单时收起其它展开项
            if (this.sidebarUniqueOpen && indexPath.length === 1) {
                const menu = this.$refs.menu
                const opened = menu.openedMenus
                opened.forEach(i => i !== index && menu.closeMenu(i))
            }

            //mobile时激活隐藏侧边栏
            this.device === 'mobile' && settingMutations.sidebarCollapse(true)

            jump && this.actionOnSelectMenu(index)
        }
    },

    beforeDestroy() {
        document.removeEventListener('mousemove', this.moveEvent)
    },

    render() {
        //如果没有菜单，则不渲染侧边栏
        if (this.sidebarMenus.length <= 0) return

        const menu = (
            <el-menu
                ref="menu"
                collapse={this.collapse}
                collapse-transition={false}
                default-active={this.activeMenu}
                unique-opened={this.sidebarUniqueOpen}
                mode="vertical"
                on-select={this.select}
            >
                {this.sidebarMenus.map(m => (
                    <sidebar-item
                        show-parent={this.sidebarShowParent}
                        collapse={this.sidebarCollapse}
                        menu={m}
                    />
                ))}
            </el-menu>
        )

        return (
            <aside
                class={this.sidebarClass}
                on-mouseenter={() => this.mouseOutside = false}
                on-mouseleave={() => this.mouseOutside = true}
            >
                {this.showLogo && <logo collapse={this.collapse}/>}
                {<el-scrollbar noresize native>{menu}</el-scrollbar>}
            </aside>
        )
    }
}
</script>

<style lang="scss" src="./style.scss"></style>

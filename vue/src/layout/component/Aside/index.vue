<script type="text/jsx">
import Vue from 'vue'
import menuMixin from "@/layout/mixin/menu"
import {getters as mainGetters} from "@/layout/store/main"
import {getters as settingGetters, mutations as settingMutations} from "@/layout/store/setting"
import Logo from './component/Logo'
import Mask from "./component/Mask"
import MenuItem from '@/component/menu/MenuItem'
import {getSidebarMenus, getActiveMenuByRoute} from "@/layout/util"

export default {
    name: 'Aside',

    mixins: [menuMixin],

    components: {Logo, MenuItem},

    data() {
        return {
            //鼠标是否在侧边栏外
            mouseOutside: true
        }
    },

    computed: {
        device: () => mainGetters.device,
        activeRootMenu: () => mainGetters.activeRootMenu,

        //侧边栏的折叠状态，true折叠false展开，仅在pc端可折叠
        collapse() {
            return settingGetters.sidebarCollapse && this.device === 'pc'
        },

        //是否隐藏侧边栏
        //①设置了侧边栏自动隐藏且鼠标不在侧边栏内且是pc端
        //②侧边栏处于折叠状态且是移动端
        hideSidebar() {
            const one = settingGetters.sidebarAutoHidden && this.mouseOutside && this.device === 'pc',
                two = settingGetters.sidebarCollapse && this.device === 'mobile'
            return one || two
        },

        //是否显示移动端展开侧边栏时的遮罩
        showHiddenMask() {
            return !settingGetters.sidebarCollapse && this.device === 'mobile'
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
                v === 'mobile' && this.collapseSidebar()
            }
        },

        //顶部菜单改变时重设高亮项
        activeRootMenu(v) {
            v && this.resetActiveMenu()
        },

        //设置了侧边栏自动隐藏后，根据状态添加或移除鼠标移动事件
        hideSidebar(v) {
            if (!settingGetters.sidebarAutoHidden) return
            const method = `${v ? 'add' : 'remove'}EventListener`
            document[method]('mousemove', this.moveEvent)
        },

        showHiddenMask(v) {
            this.maskInstance.show = v
        }
    },

    methods: {
        moveEvent(e) {
            if (e.clientX <= 1) this.mouseOutside = false
        },

        //模拟选中菜单
        onSelect(index, indexPath, item, jump = true) {
            //开启手风琴模式时，激活没有子级的菜单时收起其它展开项
            if (settingGetters.sidebarUniqueOpen && indexPath.length === 1) {
                const menu = this.$refs.menu
                const opened = menu.openedMenus
                opened.forEach(i => i !== index && menu.closeMenu(i))
            }

            jump && this.actionOnSelectMenu(index)
        },

        collapseSidebar(e) {
            if (e) {
                e.preventDefault()
                e.stopPropagation()
            }
            settingMutations.sidebarCollapse(true)
        }
    },

    mounted() {
        if (settingGetters.sidebarAutoHidden) {
            document.addEventListener('mousemove', this.moveEvent)
        }

        //插入遮罩组件
        const MaskConstructor = Vue.extend(Mask)
        this.maskInstance = new MaskConstructor().$mount()
        this.maskInstance.onClick = this.collapseSidebar
        document.body.appendChild(this.maskInstance.$el)

        this.$once('hook:beforeDestroy', () => {
            document.removeEventListener('mousemove', this.moveEvent)

            document.body.removeChild(this.maskInstance.$el)
            this.maskInstance.$destroy()
        })
    },

    render() {
        const menus = getSidebarMenus()
        if (menus.length <= 0) return

        const asideClass = {
            'aside': true,
            'mobile': this.device === 'mobile',
            'collapse': this.collapse,
            'hide': this.hideSidebar
        }

        return (
            <aside
                class={asideClass}
                on-mouseenter={() => this.mouseOutside = false}
                on-mouseleave={() => this.mouseOutside = true}
            >
                {settingGetters.showLogo && <logo collapse={this.collapse}/>}
                <el-menu
                    ref="menu"
                    class="el-menu--vertical"
                    collapse={this.collapse}
                    collapse-transition={false}
                    default-active={this.activeMenu}
                    unique-opened={settingGetters.sidebarUniqueOpen}
                    on-select={this.onSelect}
                >
                    {menus.map(m => (
                        <menu-item
                            menu={m}
                            show-parent={settingGetters.sidebarShowParent}
                            collapse={settingGetters.sidebarCollapse}
                            show-icon-max-depth={1}
                        />
                    ))}
                </el-menu>
            </aside>
        )
    }
}
</script>

<style lang="scss" src="./style.scss"></style>

<script type="text/jsx">
/**
 * 顶部菜单，参考了ant design的响应式设计
 * TODO 菜单隐藏后再显示出来存在BUG，怀疑是组件复用的问题
 */
import menuMixin from "@/layout/mixin/menu"
import {getters as appGetters, mutations as appMutations} from "@/layout/store/app"
import NavMenu from "@/component/menu/NavMenu"
import NavMenuItem from "@/component/menu/NavMenu/item"
import {getActiveMenuByRoute} from "@/layout/util"

export default {
    name: "HeadMenu",

    mixins: [menuMixin],

    components: {NavMenu, NavMenuItem},

    props: {
        //是否在只有一个顶部菜单时仍然渲染
        alwaysShow: Boolean
    },

    data() {
        return {
            //最后一个不被隐藏的顶部菜单的数组下标
            //为undefined时，说明不需要隐藏菜单，为-1时，说明需要隐藏全部菜单
            lastVisibleIndex: undefined
        }
    },

    computed: {
        navMode: () => appGetters.navMode,

        menus() {
            if (appGetters.isMobile) return []
            switch (this.navMode) {
                case 'aside':
                    return []
                case 'head' :
                    return appGetters.menus
                case 'mix':
                    return appGetters.menus.map(root => ({...root, children: undefined}))
            }
        }
    },

    watch: {
        //路由变化时设置高亮菜单
        $route: {
            immediate: true,
            handler(to) {
                const {path, matched} = to
                //使用/redirect跳转 或 无匹配路由 时跳过
                if (path.startsWith('/redirect') || matched.length === 0) {
                    return
                }
                this.setActiveMenu(this.navMode, to)
            }
        },

        //切换导航模式时重新设置高亮菜单
        navMode: {
            immediate: true,
            handler(mode) {
                this.setActiveMenu(mode)
            }
        }
    },

    methods: {
        setActiveMenu(navMode = this.navMode, {path, meta, matched} = this.$route) {
            //设置当前激活的顶部菜单
            const rootPath = matched[0].path || '/'
            appMutations.activeRootMenu(rootPath)

            //只有在混合导航模式下才将当前激活的顶部菜单认为是当前菜单
            if (navMode === 'mix') {
                this.activeMenu = rootPath
            }
            //否则按照路由配置项设置
            else this.activeMenu = getActiveMenuByRoute({path, meta})
        },
        onSelect(index) {
            //混合导航模式下，点击相同菜单不刷新页面
            if (this.navMode === 'mix' && index === appGetters.activeRootMenu) {
                return
            }
            this.actionOnSelectMenu(index)
        },

        //获取el-menu的dom
        getMenuEl() {
            return this.$el
        },
        //获取初始菜单的总宽度，只在mounted时调用一次
        setChildrenWidth() {
            const ul = this.getMenuEl()
            if (!ul) return
            const menuItemNodes = ul.children
            if (!menuItemNodes || menuItemNodes.length === 0) return
            //'更多'菜单的宽度，由于不考虑自定义，所以直接写死
            this.overflowedIndicatorWidth = 50
            this.menuItemSizes = Array.from(menuItemNodes).map(i => i.getBoundingClientRect().width)
            this.originalTotalWidth = this.menuItemSizes.reduce((acc, cur) => acc + cur, 0)
        },

        resize() {
            const width = this.getMenuEl().getBoundingClientRect().width

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
        createResizeObserver() {
            //菜单未渲染时，移除之前的observer
            if (!this.getMenuEl()) {
                if (this.resizeObserver) {
                    this.resizeObserver.disconnect()
                    this.resizeObserver = null
                }
                return
            }

            //如果已创建observer，则返回
            if (this.resizeObserver) return

            this.resizeObserver = new ResizeObserver(this.resize)
            this.resizeObserver.observe(this.getMenuEl())

            this.$once('hook:beforeDestroy', () => {
                this.resizeObserver && this.resizeObserver.disconnect()
            })
        },

        renderVisibleMenus(visibleMenus) {
            return visibleMenus.map(m => <nav-menu-item menu={m} show-icon-max-depth={1}/>)
        },
        renderHiddenMenus(hiddenMenus, hideAll) {
            if (hiddenMenus.length <= 0) return

            const content = hideAll
                ? <v-icon slot="title" icon={'el-icon-menu'}/>
                : <span slot="title" class="menu-item-content">{'...'}</span>

            const children = hiddenMenus.map(m => <nav-menu-item menu={m} show-icon-max-depth={0}/>)
            children.unshift(content)

            return <el-submenu index={'_'}>{children}</el-submenu>
        }
    },

    mounted() {
        this.setChildrenWidth()
        this.createResizeObserver()
    },

    render() {
        const {lastVisibleIndex, menus} = this
        if (!Array.isArray(menus) || menus.length <= 0 || menus.length === 1 && !this.alwaysShow) {
            return
        }

        let visibleMenus = menus, hiddenMenus = []
        if (lastVisibleIndex !== undefined) {
            //不需要隐藏菜单
            if (lastVisibleIndex === -1) {
                visibleMenus = []
                hiddenMenus = menus
            }
            //需要隐藏全部菜单
            else if (lastVisibleIndex !== menus.length - 1) {
                visibleMenus = menus.slice(0, lastVisibleIndex + 1)
                hiddenMenus = menus.slice(lastVisibleIndex + 1)
            }
        }

        return (
            <nav-menu
                mode="horizontal"
                default-active={this.activeMenu}
                on-select={this.onSelect}
            >
                {this.renderVisibleMenus(visibleMenus)}
                {this.renderHiddenMenus(hiddenMenus, hiddenMenus.length === menus.length)}
            </nav-menu>
        )
    }
}
</script>

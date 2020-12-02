<script type="text/jsx">
import menuMixin from "@/layout/mixin/menu"
import {getters as appGetters, mutations as appMutations} from "@/layout/store/app"
import {getters as asideGetters} from "@/layout/store/aside"
import {getters as pageGetters} from "@/layout/store/page"
import Logo from '@/layout/component/Logo'
import {scrollIntoViewVertically} from "@/util/browser"

const Item = {
    functional: true,

    props: {
        title: String,
        icon: String,
        active: Boolean
    },

    render(h, context) {
        const {title, icon, active} = context.props
        const {click} = context.listeners

        return (
            <li
                class={{'el-menu-item': true, 'is-active': active}}
                on-click={click}
            >
                <v-icon icon={icon}/>
                <span class="menu-item-content">{title}</span>
            </li>
        )
    }
}

export default {
    name: "RootSidebar",

    mixins: [menuMixin],

    components: {Logo, Item},

    data() {
        return {
            //鼠标是否在外部，用于判断是否需要展开折叠
            mouseOutside: true
        }
    },

    computed: {
        menus() {
            return appGetters.menus.map(root => ({...root, children: undefined}))
        },

        //是否需要显示logo
        showLogo() {
            return pageGetters.showLogo && pageGetters.position === 'left-right'
        },

        containerClass() {
            return {'root-sidebar': true, 'collapse': this.mouseOutside}
        },

        menuClass() {
            return `el-menu el-menu--vertical el-menu--${asideGetters.theme}`
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

                this.setActiveRootMenu(this.navMode, to)

                //滚动至激活菜单
                if (!this._isMounted) return
                this.$nextTick(() => {
                    scrollIntoViewVertically(
                        this.$el.querySelector('.el-menu-item.is-active'),
                        this.$el.querySelector('.el-menu.el-menu--vertical')
                    )
                })
            }
        }
    },

    methods: {
        //设置当前激活的顶部菜单
        setActiveRootMenu(navMode = this.navMode, {matched} = this.$route) {
            const rootPath = matched[0].path || '/'
            appMutations.activeRootMenu(rootPath)
        },
        onSelect(index) {
            //点击相同菜单不刷新页面
            if (index !== appGetters.activeRootMenu) {
                this.actionOnSelectMenu(index)
            }

            //收起
            this.mouseOutside = true
        },
    },

    render() {
        return (
            <div class="root-sidebar-container">
                <div
                    class={this.containerClass}
                    on-mouseleave={() => this.mouseOutside = true}
                    on-mouseenter={() => this.mouseOutside = false}
                >
                    {this.showLogo && <logo show-title={!this.mouseOutside}/>}
                    <ul class={this.menuClass}>
                        {this.menus.map(({fullPath, meta: {title, icon}}) => (
                            <item
                                title={title}
                                icon={icon}
                                active={fullPath === appGetters.activeRootMenu}
                                on-click={() => this.onSelect(fullPath)}
                            />
                        ))}
                    </ul>
                </div>
            </div>
        )
    }
}
</script>

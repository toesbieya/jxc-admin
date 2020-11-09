<script type="text/jsx">
import menuMixin from "@/layout/mixin/menu"
import {getters as appGetters, mutations as appMutations} from "@/layout/store/app"
import {getters as asideGetters} from "@/layout/store/aside"
import Logo from '@/layout/component/Aside/Logo'

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

        const iconComponent =
            icon
                ? <v-icon icon={icon}/>
                : <i class="icon" style="font-style: normal">{title[0]}</i>

        return (
            <li
                class={{'el-menu-item': true, 'is-active': active}}
                on-click={click}
            >
                {iconComponent}
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
                    class={{'root-sidebar': true, 'collapse': this.mouseOutside}}
                    on-mouseleave={() => this.mouseOutside = true}
                    on-mouseenter={() => this.mouseOutside = false}
                >
                    {asideGetters.showLogo && <logo collapse={this.mouseOutside}/>}
                    <ul class="el-menu el-menu--vertical">
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

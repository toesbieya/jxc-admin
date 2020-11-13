<script type="text/jsx">
import menuMixin from "@/layout/mixin/menu"
import {getters as appGetters, mutations as appMutations} from "@/layout/store/app"
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

        const iconComponent =
            icon
                ? <v-icon icon={icon}/>
                : <em class="icon" style="font-style: normal">{title[0]}</em>

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
        const showLogo = pageGetters.showLogo && pageGetters.logoPosition === 'aside'

        return (
            <div class="root-sidebar-container">
                <div
                    class={{'root-sidebar': true, 'collapse': this.mouseOutside}}
                    on-mouseleave={() => this.mouseOutside = true}
                    on-mouseenter={() => this.mouseOutside = false}
                >
                    {showLogo && <logo show-title={!this.mouseOutside}/>}
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

<style lang="scss">
@import "~@/asset/style/variables.scss";

$background-color: #1d2935;

.root-sidebar-container {
    width: $aside-icon-size + 20px * 2;
    position: relative;
}

.root-sidebar {
    display: flex;
    flex-direction: column;
    position: absolute;
    height: 100vh;
    width: $aside-width;
    background-color: $background-color;
    overflow: hidden;
    z-index: 11;
    border-right: 1px solid #101117;
    transition: all 0.2s ease-in-out;

    > .el-menu--vertical.el-menu {
        background-color: $background-color;
    }

    //折叠状态
    &.collapse {
        width: $aside-icon-size + 20px * 2;

        .menu-item-content {
            display: none;
        }
    }
}
</style>

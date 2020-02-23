<script type="text/jsx">
    import path from 'path'
    import SidebarItemContent from './SidebarItemContent'

    export default {
        name: 'SidebarItem',
        inject: ['rootMenu'],
        components: {SidebarItemContent},
        props: {
            item: Object,
            isNest: Boolean,
            showParent: Boolean,
            basePath: {
                type: String,
                default: ''
            }
        },
        data: () => ({onlyOneChild: {}}),
        methods: {
            resolvePath(routePath = '') {
                const indexOf = this.basePath.indexOf(routePath)
                if (indexOf > 0 && this.basePath.length === indexOf + routePath.length) {
                    return this.basePath
                }
                return path.resolve(this.basePath, routePath)
            }
        },
        render() {
            const hasOnlyOneShowingChild = () => {
                const showingChildren = this.item.children || []

                if (showingChildren.length === 1) {
                    this.onlyOneChild = showingChildren[0]
                    return true
                }

                if (showingChildren.length <= 0) {
                    this.onlyOneChild = {...this.item, path: undefined, children: undefined}
                    return true
                }

                return false
            }
            const showSingle = () => {
                return hasOnlyOneShowingChild() && (!this.onlyOneChild.children) && !this.item.alwaysShow
            }
            if (showSingle()) {
                const index = this.resolvePath(this.onlyOneChild.path)
                const icon = this.onlyOneChild.meta.icon || (this.item.meta && this.item.meta.icon)
                const title = this.onlyOneChild.meta.title
                return (
                    <el-menu-item index={index} class={{'submenu-title-noDropdown': !this.isNest}}>
                        <sidebar-item-content icon={icon} title={title}/>
                    </el-menu-item>
                )
            }
            else {
                const index = this.resolvePath(this.item.path)
                const icon = (this.item.meta && this.item.meta.icon) || (this.onlyOneChild.meta && this.onlyOneChild.meta.icon)
                const title = this.item.meta.title

                const children = this.item.children.map(child => (
                    <sidebar-item
                        is-nest
                        item={child}
                        base-path={this.resolvePath(child.path)}
                        class="nest-menu"
                    />
                ))

                //弹出菜单包裹滚动条
                const popoverMenu = [<el-scrollbar wrap-class="popover-menu__wrap">{children}</el-scrollbar>]

                //弹出菜单显示父级信息
                if (this.rootMenu.collapse && this.showParent) {
                    popoverMenu.unshift(
                        <el-menu-item class="popover-menu__title" disabled>
                            <sidebar-item-content icon={icon} title={title}/>
                        </el-menu-item>
                    )
                }
                //外面需要额外包裹一层，否则el-submenu调用this.$parent.$el.dispatch时会死循环
                return (
                    <li>
                        <el-submenu index={index} popper-append-to-body>
                            <sidebar-item-content slot="title" icon={icon} title={title}/>
                            {this.rootMenu.collapse ? popoverMenu : children}
                        </el-submenu>
                    </li>
                )
            }
        }
    }
</script>

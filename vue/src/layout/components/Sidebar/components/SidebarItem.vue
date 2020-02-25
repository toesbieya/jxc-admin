<script type="text/jsx">
    import path from 'path'
    import SidebarItemContent from './SidebarItemContent'

    function resolvePath(basePath, routePath = '') {
        const indexOf = basePath.indexOf(routePath)
        const samePath = basePath.length === indexOf + routePath.length
        return indexOf && samePath ? basePath : path.resolve(basePath, routePath)
    }

    function getOnlyChild(item) {
        const children = item.children || []

        if (children.length === 1) return children[0]

        if (children.length < 1) return {...item, path: undefined, children: undefined}

        return null
    }

    export default {
        functional: true,
        props: {
            item: Object,
            isNest: Boolean,
            showParent: Boolean,
            collapse: Boolean,
            basePath: {type: String, default: ''}
        },
        render(h, context) {
            function renderNode({item, isNest, showParent, collapse, basePath}) {
                let onlyOneChild = getOnlyChild(item)

                const showSingle = onlyOneChild && !onlyOneChild.children

                if (showSingle) {
                    const index = resolvePath(basePath, onlyOneChild.path)
                    const icon = onlyOneChild.meta.icon || (item.meta && item.meta.icon)
                    const title = onlyOneChild.meta.title
                    return (
                        <el-menu-item
                            index={index}
                            class={{'submenu-title-noDropdown': !isNest, 'nest-menu': isNest}}
                        >
                            <SidebarItemContent icon={icon} title={title}/>
                        </el-menu-item>
                    )
                }
                else {
                    const index = resolvePath(basePath, item.path)
                    const icon = (item.meta && item.meta.icon) || (onlyOneChild.meta && onlyOneChild.meta.icon)
                    const title = item.meta.title

                    const children = item.children.map(child => renderNode({
                        isNest: true,
                        item: child,
                        basePath: resolvePath(basePath, child.path),
                        showParent,
                        collapse
                    }))

                    //弹出菜单如果包裹滚动条，则在触发mouseleave时，不会触发父菜单的mouseleave事件
                    if (collapse) {
                        //弹出菜单显示父级信息
                        if (showParent) {
                            children.unshift(
                                <div class="popover-menu__title el-menu-item">
                                    <SidebarItemContent icon={icon} title={title}/>
                                </div>
                            )
                        }
                    }

                    return (
                        <el-submenu class={{'nest-menu': isNest}} index={index} popper-append-to-body>
                            <SidebarItemContent slot="title" icon={icon} title={title}/>
                            {children}
                        </el-submenu>
                    )
                }
            }

            return renderNode(context.props)
        }
    }
</script>

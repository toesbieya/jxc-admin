<script type="text/jsx">
import SidebarItemContent from './SidebarItemContent'

function getOnlyChild(item) {
    const children = item.children || []

    if (children.length === 1) return item.alwaysShow ? null : children[0]

    if (children.length < 1) return {...item, path: undefined, children: undefined}

    return null
}

function renderNode(h, {item, showParent, collapse}) {
    let onlyOneChild = getOnlyChild(item)

    const showSingle = onlyOneChild && !onlyOneChild.children

    if (showSingle) {
        const {icon, title} = onlyOneChild.meta

        return (
            <el-menu-item index={onlyOneChild.fullPath}>
                <SidebarItemContent icon={icon} title={title}/>
            </el-menu-item>
        )
    }
    else {
        const {icon, title} = item.meta

        let children = item.children.map(child => renderNode(h, {
            item: child,
            showParent,
            collapse
        }))

        //弹出菜单如果包裹滚动条，则在触发mouseleave时，不会触发父菜单的mouseleave事件
        if (collapse) {
            //弹出菜单显示父级信息
            if (showParent) {
                children = [
                    <div class="popover-menu__title el-menu-item">
                        <SidebarItemContent icon={icon} title={title}/>
                    </div>,
                    <div class="el-menu el-menu--inline">{children}</div>
                ]
            }
        }

        return (
            <el-submenu index={item.fullPath} popper-append-to-body>
                <SidebarItemContent slot="title" icon={icon} title={title}/>
                {children}
            </el-submenu>
        )
    }
}

export default {
    functional: true,

    props: {
        item: Object,
        showParent: Boolean,
        collapse: Boolean
    },

    render(h, context) {
        return renderNode(h, context.props)
    }
}
</script>

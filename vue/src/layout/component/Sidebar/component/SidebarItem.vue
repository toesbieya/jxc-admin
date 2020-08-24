<script type="text/jsx">
import SidebarItemContent from './SidebarItemContent'

//获取不需要嵌套展示的菜单
function getOnlyChild(menu) {
    const {children = [], meta: {alwaysShow} = {}} = menu

    if (!children.length) return {...menu, children: undefined}

    if (children.length === 1) return alwaysShow ? null : getOnlyChild(children[0])

    return null
}

function renderSingleMenu(h, {index, icon, title}) {
    return (
        <el-menu-item index={index}>
            <SidebarItemContent icon={icon} title={title}/>
        </el-menu-item>
    )
}

function renderSubMenu(h, {index, icon, title, children}) {
    return (
        <el-submenu index={index} popper-append-to-body>
            <SidebarItemContent slot="title" icon={icon} title={title}/>
            {children}
        </el-submenu>
    )
}

function renderChildrenWithParentMenu(h, {icon, title, children}) {
    return [
        <div class="popover-menu__title el-menu-item">
            <SidebarItemContent icon={icon} title={title}/>
        </div>,
        <div class="el-menu el-menu--inline">{children}</div>
    ]
}

function renderMenu(h, {menu, showParent, collapse}) {
    const onlyOneChild = getOnlyChild(menu)

    const showSingle = onlyOneChild && !onlyOneChild.children

    if (showSingle) {
        const {icon, title} = onlyOneChild.meta
        return renderSingleMenu(h, {index: onlyOneChild.fullPath, icon, title})
    }

    const {icon, title} = menu.meta

    let children = menu.children.map(child => renderMenu(h, {
        menu: child,
        showParent,
        collapse
    }))

    //这里弹出菜单如果包裹了<el-scrollbar>，则在触发mouseleave时，不会触发父菜单的mouseleave事件
    if (collapse) {
        //弹出菜单显示父级信息
        if (showParent) {
            children = renderChildrenWithParentMenu(h, {icon, title, children})
        }
    }

    return renderSubMenu(h, {index: menu.fullPath, icon, title, children})
}

export default {
    functional: true,

    props: {
        menu: Object,
        showParent: Boolean,
        collapse: Boolean
    },

    render(h, context) {
        return renderMenu(h, context.props)
    }
}
</script>

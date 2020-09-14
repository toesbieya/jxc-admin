<script type="text/jsx">
const MenuItemContent = {
    name: "NavMenuItemContent",

    functional: true,

    props: {icon: String, title: String, useTitleAsIcon: Boolean},

    render(h, context) {
        const {icon, title, useTitleAsIcon} = context.props

        const iconComponent =
            useTitleAsIcon
                ? <em>{title[0]}</em>
                : icon
                ? <v-icon icon={icon}/>
                : <span class="icon" style="display: none"/>

        const vnodes = [iconComponent]

        title && vnodes.push(<span slot="title" class="menu-item-content">{title}</span>)

        return vnodes
    }
}

//根据showIconMaxDepth、depth判断是否需要限制图标的显示
function getIcon({icon, showIconMaxDepth, depth}) {
    if (!showIconMaxDepth || showIconMaxDepth < 0) {
        return icon
    }
    return showIconMaxDepth < depth ? undefined : icon
}

//获取不需要嵌套展示的菜单
function getOnlyChild(menu) {
    const {children = [], meta: {alwaysShow} = {}} = menu

    if (!children.length) return {...menu, children: undefined}

    if (children.length === 1) return alwaysShow ? null : getOnlyChild(children[0])

    return null
}

function renderSingleMenu(h, {index, icon, title, collapse, depth}) {
    const useTitleAsIcon = depth === 1 && collapse && !!!icon && !!title
    return (
        <el-menu-item key={index} index={index}>
            <MenuItemContent icon={icon} title={title} useTitleAsIcon={useTitleAsIcon}/>
        </el-menu-item>
    )
}

function renderSubMenu(h, {index, icon, title, children, collapse, depth}) {
    const useTitleAsIcon = depth === 1 && collapse && !!!icon && !!title
    return (
        <el-submenu key={index} index={index} popper-append-to-body>
            <MenuItemContent slot="title" icon={icon} title={title} useTitleAsIcon={useTitleAsIcon}/>
            {children}
        </el-submenu>
    )
}

function renderChildrenWithParentMenu(h, {icon, title, children}) {
    return [
        <div class="popover-menu__title el-menu-item">
            <MenuItemContent icon={icon} title={title}/>
        </div>,
        <div class="el-menu el-menu--inline">{children}</div>
    ]
}

function renderMenu(h, {menu, showParent, collapse, showIconMaxDepth, depth = 1}) {
    const onlyOneChild = getOnlyChild(menu)

    const showSingle = onlyOneChild && !onlyOneChild.children

    if (showSingle) {
        const {fullPath, meta: {icon, title}} = onlyOneChild
        return renderSingleMenu(h, {
            index: fullPath,
            icon: getIcon({icon, showIconMaxDepth, depth}),
            title,
            collapse,
            depth
        })
    }

    const {icon, title} = menu.meta

    let children = menu.children.map(child => renderMenu(h, {
        menu: child,
        showParent,
        collapse,
        showIconMaxDepth,
        depth: depth + 1
    }))

    //这里弹出菜单如果包裹了<el-scrollbar>，则在触发mouseleave时，不会触发父菜单的mouseleave事件
    if (collapse) {
        //弹出菜单显示父级信息
        if (showParent) {
            children = renderChildrenWithParentMenu(h, {icon, title, children})
        }
    }

    return renderSubMenu(h, {
        index: menu.fullPath,
        icon: getIcon({icon, showIconMaxDepth, depth}),
        title,
        children,
        collapse,
        depth
    })
}

export default {
    name: "NavMenuItem",

    functional: true,

    props: {
        //菜单对象，即路由配置项
        menu: Object,

        //折叠时的展开菜单是否显示父级
        showParent: Boolean,

        //是否为折叠状态
        collapse: Boolean,

        //能够显示图标的最大深度，不传 或 <0 则不作限制
        showIconMaxDepth: Number
    },

    render(h, context) {
        return renderMenu(h, context.props)
    }
}
</script>

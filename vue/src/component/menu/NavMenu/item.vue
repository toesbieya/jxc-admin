<script type="text/jsx">
const MenuItemContent = {
    functional: true,

    props: {icon: String, title: String},

    render(h, context) {
        const {icon, title} = context.props

        return [<v-icon icon={icon}/>, <span slot="title" class="menu-item-content">{title}</span>]
    }
}

//根据showIconMaxDepth、depth判断是否需要限制图标的显示
function getIcon({icon, showIconMaxDepth, depth}) {
    if (showIconMaxDepth == null || showIconMaxDepth < 0) {
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

function renderSingleMenu(h, {index, icon, title}) {
    return (
        <el-menu-item key={index} index={index}>
            <MenuItemContent icon={icon} title={title}/>
        </el-menu-item>
    )
}

function renderSubMenu(h, {index, icon, title, popperClass, children}) {
    return (
        <el-submenu key={index} index={index} popper-class={popperClass}>
            <MenuItemContent slot="title" icon={icon} title={title}/>
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

function renderMenu(h, props) {
    const {menu, popperClass, showParent, collapse, showIconMaxDepth, depth = 1} = props

    const onlyOneChild = getOnlyChild(menu)

    const showSingle = onlyOneChild && !onlyOneChild.children

    if (showSingle) {
        const {fullPath, meta: {icon, title}} = onlyOneChild
        return renderSingleMenu(h, {
            index: fullPath,
            icon: getIcon({icon, showIconMaxDepth, depth}),
            title
        })
    }

    const {icon, title} = menu.meta

    let children = menu.children.map(child => renderMenu(h, {...props, menu: child, depth: depth + 1}))

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
        popperClass,
        children
    })
}

export default {
    functional: true,

    props: {
        //菜单对象，即路由配置项
        menu: Object,

        //弹出菜单的自定义类名
        popperClass: String,

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

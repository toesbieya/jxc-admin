<script type="text/jsx">
import {isEmpty} from "@/util"

const MenuItemContent = {
    functional: true,

    props: {
        icon: String,
        title: String,
        highlight: String
    },

    render(h, context) {
        const {icon, title, highlight} = context.props
        const content = isEmpty(highlight)
            ? title
            : getHighlightContent(h, title, highlight)

        return [
            <v-icon icon={icon}/>,
            <span slot="title" class="menu-item-content">{content}</span>
        ]
    }
}

//获得高亮的菜单内容vnode
function getHighlightContent(h, title, highlight) {
    const start = title.indexOf(highlight)

    if (start === -1) return title

    const end = start + highlight.length

    return [
        title.substring(0, start),
        <span class="menu-highlight-result">{title.substring(start, end)}</span>,
        title.substring(end)
    ]
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

function renderSingleMenu(h, {index, inlineIndent, icon, title, highlight}) {
    return (
        <el-menu-item key={index} index={index} inline-indent={inlineIndent}>
            <MenuItemContent icon={icon} title={title} highlight={highlight}/>
        </el-menu-item>
    )
}

function renderSubMenu(h, {index, inlineIndent, icon, title, popperClass, highlight, children}) {
    return (
        <el-submenu key={index} index={index} inline-indent={inlineIndent} popper-class={popperClass}>
            <MenuItemContent slot="title" icon={icon} title={title} highlight={highlight}/>
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
    const {menu, inlineIndent, popperClass, highlight, showParent, collapse, showIconMaxDepth, depth = 1} = props

    const onlyOneChild = getOnlyChild(menu)

    const showSingle = onlyOneChild && !onlyOneChild.children

    if (showSingle) {
        const {fullPath, meta: {icon, title}} = onlyOneChild
        return renderSingleMenu(h, {
            index: fullPath,
            inlineIndent,
            icon: getIcon({icon, showIconMaxDepth, depth}),
            title,
            highlight
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
        inlineIndent,
        icon: getIcon({icon, showIconMaxDepth, depth}),
        title,
        popperClass,
        highlight,
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

        //需要高亮的字符串
        highlight: String,

        //折叠时的展开菜单是否显示父级
        showParent: Boolean,

        //向左缩进的单位距离
        inlineIndent: Number,

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

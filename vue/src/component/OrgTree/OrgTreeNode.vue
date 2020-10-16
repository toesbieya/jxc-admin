<script type="text/jsx">
// 判断是否叶子节点
const isLeaf = (data, prop) => {
    return !Array.isArray(data[prop]) || data[prop].length === 0
}

// 创建 node 节点
const renderNode = (h, data, context) => {
    const {props} = context
    const cls = ['org-tree-node']
    const childNodes = []
    const children = data[props.props.children]

    if (isLeaf(data, props.props.children)) {
        cls.push('is-leaf')
    }
    else if (props.collapsable && !data[props.props.expand]) {
        cls.push('collapsed')
    }

    childNodes.push(renderLabel(h, data, context))

    if (!props.collapsable || data[props.props.expand]) {
        childNodes.push(renderChildren(h, children, context))
    }

    return <div class={cls.join(' ')}>{childNodes}</div>
}

// 创建 label 节点
const renderLabel = (h, data, context) => {
    const {props} = context
    const label = data[props.props.label]
    const clickHandler = context.listeners['on-node-click']
    const mousedownHandler = e => {
        e.stopPropagation()
        if (context.listeners['on-node-mousedown']) {
            context.listeners['on-node-mousedown'](e, data)
        }
    }
    const mouseupHandler = context.listeners['on-node-mouseup']
    const touchstartHandler = context.listeners['on-node-touchstart']
    const touchleaveHandler = context.listeners['on-node-touchleave']

    const childNodes = []
    if (context.parent.$scopedSlots.default) {
        childNodes.push(context.parent.$scopedSlots.default(data))
    }
    else childNodes.push(label)

    if (props.collapsable && !isLeaf(data, props.props.children)) {
        childNodes.push(renderBtn(h, data, context))
    }

    const cls = ['org-tree-node-label-inner']
    let {labelWidth, labelClassName} = props
    if (typeof labelWidth === 'number') {
        labelWidth += 'px'
    }
    if (typeof labelClassName === 'function') {
        labelClassName = labelClassName(data)
    }
    labelClassName && cls.push(labelClassName)

    return (
        <div
            class="org-tree-node-label"
            on-click={e => clickHandler && clickHandler(e, data)}
            on-mousedown={mousedownHandler}
            on-mouseup={e => mouseupHandler && mouseupHandler(e, data)}
            on-touchstart={e => touchstartHandler && touchstartHandler(e, data)}
            on-touchleave={e => touchleaveHandler && touchleaveHandler(e, data)}
        >
            <div class={cls.join(' ')} style={{width: labelWidth}}>
                {childNodes}
            </div>
        </div>
    )
}

// 创建展开折叠按钮
const renderBtn = (h, data, context) => {
    const {props} = context
    const expandHandler = context.listeners['on-expand']
    const onClock = e => {
        e.stopPropagation()
        expandHandler && expandHandler(data)
    }
    const onMousedown = e => e.stopPropagation()
    const cls = ['org-tree-node-btn']

    data[props.props.expand] && cls.push('expanded')

    return (
        <span class="org-tree-button-wrapper" on-click={onClock} on-mousedown={onMousedown}>
            <span class={cls.join(' ')}/>
        </span>
    )
}

// 创建 node 子节点
const renderChildren = (h, list, context) => {
    if (Array.isArray(list) && list.length) {
        return (
            <div class="org-tree-node-children">
                {list.map(item => renderNode(h, item, context))}
            </div>
        )
    }
}

export default {
    name: "OrgTreeNode",

    functional: true,

    render(h, context) {
        return renderNode(h, context.props.data, context)
    }
}
</script>

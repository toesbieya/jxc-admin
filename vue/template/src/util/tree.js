/**
 * 根据判断函数裁剪树，当节点不满足predicate且无下级节点时将被裁剪
 * 此方法会改变原数组的children属性！
 *
 * @param tree {array}            原始树
 * @param predicate {function}    返回false时移除该节点，传入当前节点
 * @param childrenKey {string}    节点中代表子节点数组含义的字段名称，比如[{ch:[...]}]就是'ch'
 * @return {array}                经过裁剪后的树
 */
export function shakeTree(tree, predicate = () => true, childrenKey = 'children') {
    if (!tree) return undefined

    return tree.filter(data => {
        const children = shakeTree(data[childrenKey], predicate, childrenKey)

        if (children) {
            data[childrenKey] = children
        }

        return predicate(data) || children && children.length > 0
    })
}

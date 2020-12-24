/**
 * el-tree展开折叠控制
 *
 * @param ref      el-tree实例
 * @param action  'expand' | 'collapse'
 * @param level   展开的节点最大深度，为0时匹配全部节点
 * @param func    自定义展开的函数，传入(node, index)，返回boolean，优先使用
 */
export function expandControl(ref, action = 'expand', level = 1, func) {
    const handler = function () {
        if (typeof func === 'function') return func

        const expand = action === 'expand'
        const forAll = level === 0

        return node => node.expanded = forAll || node.level <= level ? expand : !expand
    }()

    ref.store._getAllNodes().forEach(handler)
}

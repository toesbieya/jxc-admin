import {deepClone} from "@/util"
import {createWorker} from '@/util/worker'

const DEFAULT_PROPS = {
    id: 'id',
    pid: 'pid',
    children: 'children',
    rootPredicate: 0,
    leafHasChildren: false
}

/**
 * 列表转树形结构
 *
 * @param list {array}
 * @param props 树的配置项
 *     @param props.id {string} 节点的id属性名称
 *     @param props.pid {string} 节点的父级id属性名称
 *     @param props.children {string} 节点的子级节点属性名称
 *     @param props.rootPredicate {*|function} 根节点的判定方法，传入函数时参数为节点对象
 *     @param props.leafHasChildren {boolean} 叶子节点是否能有children属性
 * @return {array}
 */
export function createTree(list, props = {}) {
    if (!list || list.length <= 0) return []

    const {
        id = DEFAULT_PROPS.id,
        pid = DEFAULT_PROPS.pid,
        children = DEFAULT_PROPS.children,
        rootPredicate = DEFAULT_PROPS.rootPredicate,
        leafHasChildren = DEFAULT_PROPS.leafHasChildren
    } = props

    const info = {}

    list.forEach(i => {
        info[i[id]] = i
        if (leafHasChildren) i[children] = []
    })

    const predicate = (() => {
        return typeof rootPredicate === 'function'
            ? rootPredicate
            : key => key === rootPredicate
    })()

    return list.filter(node => {
        const key = node[pid]
        const parent = info[key]
        if (parent) {
            if (!parent[children]) parent[children] = []
            parent[children].push(node)
        }
        return predicate(key, node)
    })
}

/**
 * 完全树full，拿到某些带value的节点数组limit，获得裁剪后的树
 * 只用于裁剪客户和供应商的行政区域树
 *
 * @param full {array}
 * @param limit {array}
 */
export function createLimitTree(full, limit) {
    const map = limit.reduce((m, n) => {
        m[n.id] = n.value
        return m
    }, {})

    full = deepClone(full)

    const result = shakeTree(full, node => {
        const value = map[node.id]
        if (value !== undefined) {
            node.value = value
            return true
        }
        return false
    })

    result.forEach(i => calc(i))

    return result
}

/**
 * createLimitTree的另一版本
 *
 * @param fullMap 完全树的节点map
 * @param limit {array}
 */
export function createLimitTreeByMap(fullMap, limit) {
    const resultNodes = {}

    //从该节点往上查找父节点
    function findParent(node) {
        //如果该节点已存在于结果集中，说明包含该节点的上级分支也全部存在，所以跳过
        if (resultNodes[node.id]) return

        //使用full中的数据，仅保留node的value
        const fullNode = fullMap[node.id]
        if (!fullNode) return
        resultNodes[fullNode.id] = {...node, ...fullNode}

        //查找父节点
        const parent = fullMap[fullNode.pid]
        if (!parent) return

        return findParent(parent)
    }

    for (const node of limit) {
        if (!fullMap[node.id]) continue
        findParent(node)
    }

    const result = createTree(Object.values(resultNodes), {rootPredicate: '0'})

    result.forEach(i => calc(i))

    return result
}

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

/**
 * 自底向上计算每个节点的value
 *
 * @param node
 * @param valueKey {string}
 * @param childrenKey {string}
 * @return {number}
 */
function calc(node, valueKey = 'value', childrenKey = 'children') {
    if (!node) return 0

    const children = node[childrenKey]
    const childValue = node[valueKey] || 0

    if (!children) return childValue

    const value = childValue + children.reduce((v, child) => v + calc(child), 0)

    node[valueKey] = value

    return value
}

//树转一维id数组
export function getNodeId(arr) {
    if (!arr) return []

    const res = []

    arr.forEach(i => {
        res.push(i.id)
        if (i.children && i.children.length > 0) {
            res.push(...getNodeId(i.children))
        }
    })

    return res
}

//树转一维数组
export function flatTree(tree, childrenKey = 'children') {
    const result = []
    tree.forEach(node => {
        if (node[childrenKey]) {
            result.push(node)
            result.push.apply(result, flatTree(node[childrenKey], childrenKey))
        }
        else result.push(node)
    })
    return result
}

export function getNodesByDfs(node) {
    const nodes = []
    const stack = [node]

    while (stack.length > 0) {
        const item = stack.pop()
        nodes.push(item)
        const children = item.children
        for (let i = children.length - 1; i >= 0; i--) {
            stack.push(children[i])
        }
    }

    return nodes
}

export function getNodesByBfs(node) {
    const nodes = []
    const queue = [node]

    while (queue.length > 0) {
        const item = queue.shift()
        nodes.push(item)
        queue.push(...item.children)
    }

    return nodes
}

//借助worker生成树
export function createTreeByWorker(list) {
    return new Promise(resolve => {
        const worker = createWorker(workerTree, list, ({data}) => {
            resolve(data)
            worker.terminate()
        })
    })
}

function workerTree() {
    function createTree(list, rootSign = 0, idKey = 'id', pidKey = 'pid', childrenKey = 'children') {
        if (!list || list.length <= 0) return []
        let info = {}
        list.forEach(i => {
            i[childrenKey] = []
            info[i[idKey]] = i
        })
        return list.filter(node => {
            const key = node[pidKey]
            info[key] && info[key][childrenKey].push(node)
            return key === rootSign
        })
    }

    self.addEventListener('message', ({data}) => {
        self.postMessage(createTree(data))
    })
}

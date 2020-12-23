/**
 * 根据组件标签名称获取组件实例
 * 使用广度优先搜索
 *
 * @param instance  从哪个组件实例开始查找，一般是this
 * @param tag       要查找的组件的标签名称，可以是Function
 * @param maxDepth  查找的最大深度，不传或者<1时不作限制
 */
export function findComponentByTag(instance, tag, maxDepth) {
    if (!instance || !tag) return

    const queue = [instance]
    const predicate = typeof tag === 'function' ? tag : componentTag => componentTag === tag

    let currentDepth = 0

    while (queue.length > 0) {
        const len = queue.length

        for (let i = 0; i < len; i++) {
            const item = queue.shift()
            const {$options, $children} = item

            if (predicate($options._componentTag)) {
                return item
            }

            if (Array.isArray($children)) {
                queue.push(...$children)
            }
        }

        currentDepth++

        //超出最大深度时退出
        if (maxDepth != null && maxDepth >= 1 && maxDepth <= currentDepth) {
            return
        }
    }
}

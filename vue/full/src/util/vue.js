/**
 * 根据组件标签名称获取组件实例
 * 使用广度优先搜索
 *
 * @param instance  从哪个组件实例开始查找，一般是this
 * @param tag       要查找的组件的标签名称
 */
export function findComponentByTag(instance, tag) {
    if (!instance || !tag) return

    const queue = [instance]

    while (queue.length > 0) {
        const item = queue.shift()
        const {$options, $children} = item

        if ($options._componentTag === tag) return item

        if (Array.isArray($children)) {
            queue.push(...$children)
        }
    }
}

export const nodeType = [
    {label: '顶部菜单', value: 0, icon: 'el-icon-s-promotion'},
    {label: '聚合菜单', value: 1, icon: 'el-icon-folder'},
    {label: '页面菜单', value: 2, icon: 'el-icon-document'},
    {label: '数据接口', value: 3, icon: 'el-icon-open'},
]

export function getNodeInfo({data: {type}} = {}) {
    return nodeType.find(i => i.value === type)
}

export function getNodeTitle({data: {type, name, meta}}) {
    return type === 3 ? name : meta.title
}

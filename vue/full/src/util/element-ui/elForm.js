export const clearableComponentTag = [
    'el-input',
    'el-select',
    'el-cascader',
    'el-date-picker',
    'el-time-picker',
    'el-time-select'
]

/**
 * 模拟el-input、el-select、el-cascader、el-date-picker等具有可清空功能的组件的clear方法
 *
 * @param componentInstance 组件实例
 */
export function clearFormItem(componentInstance) {
    switch (componentInstance.$options._componentTag) {
        case 'el-input':
            componentInstance.clear()
            break
        case 'el-select':
            componentInstance.deleteSelected({stopPropagation: () => undefined})
            break
        case 'el-cascader':
            componentInstance.handleClear()
            break
        case 'el-date-picker':
        case 'el-time-picker':
        case 'el-time-select':
            componentInstance.showClose = true
            componentInstance.handleClickIcon({stopPropagation: () => undefined})
            break
        default:
            break
    }
}

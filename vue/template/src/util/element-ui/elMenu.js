import {scrollIntoViewVertically} from "@/util/browser"

/**
 * 将当前激活的菜单移动到视窗中（仅限垂直菜单）
 *
 * @param menu el-menu实例
 */
export function moveToActiveMenuVertically(menu) {
    if (!menu) return

    const cur = menu.activeIndex
    if (!cur) return

    const curInstance = menu.items[cur]
    if (!curInstance) return

    let el = curInstance.$el

    //当侧边栏折叠时，需要滚动至可视区域的元素是激活菜单的最顶层父节点
    if (menu.collapse) {
        let rootParent = curInstance
        while (rootParent.$parent.$options.componentName !== 'ElMenu') {
            rootParent = rootParent.$parent
        }
        el = rootParent.$el
    }

    /*
    * 这里考虑了菜单展开时的200ms动画时间
    * 为什么不分情况讨论？比如当subMenu已经是展开状态时，无需延时滚动
    * 但这种情况无法判断，因为这时menu.openedMenus已经包含了subMenu，无论subMenu之前是否展开
    * 所以统一延时300ms
    * */
    window.setTimeout(() => scrollIntoViewVertically(el, menu.$el), 300)
}

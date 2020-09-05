import {getters as mainGetters} from "@/layout/store/main"
import {getters as settingGetters} from "@/layout/store/setting"

//获取侧边栏的菜单
export function getSidebarMenus() {
    const menus = mainGetters.menus
    if (!Array.isArray(menus)) {
        return []
    }
    if (mainGetters.device === 'mobile') {
        return menus
    }
    switch (settingGetters.navMode) {
        case 'aside':
            return menus
        case 'head':
            return []
        case 'mix':
            const root = menus.find(i => i.path === mainGetters.activeRootMenu)
            return root ? root.children || [] : []
    }
}

//根据路由获取当前激活的菜单
export function getActiveMenuByRoute({path, meta}) {
    return meta.activeMenu || path
}

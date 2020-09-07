import {getters as appGetters} from "@/layout/store/app"

//获取路由页面缓存所需的key
export function getRouterViewCacheKey({name, path, fullPath, meta = {}}) {
    const {usePathKey, useFullPathKey} = meta
    return usePathKey ? path : useFullPathKey ? fullPath : name
}

//获取侧边栏的菜单
export function getSidebarMenus() {
    const menus = appGetters.menus
    if (!Array.isArray(menus)) {
        return []
    }
    if (appGetters.device === 'mobile') {
        return menus
    }
    switch (appGetters.navMode) {
        case 'aside':
            return menus
        case 'head':
            return []
        case 'mix':
            const root = menus.find(i => i.path === appGetters.activeRootMenu)
            return root ? root.children || [] : []
    }
}

//根据路由获取当前激活的菜单
export function getActiveMenuByRoute({path, meta}) {
    return meta.activeMenu || path
}

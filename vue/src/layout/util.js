import {getters as appGetters} from "@/layout/store/app"

//获取路由页面缓存所需的key
export function getRouterViewCacheKey({name, path, fullPath, meta = {}}) {
    const {usePathKey, useFullPathKey} = meta
    return usePathKey ? path : useFullPathKey ? fullPath : name
}

//获取侧边栏的菜单，如果是双层侧边栏导航时，获取的是子菜单
export function getSidebarMenus() {
    const menus = appGetters.menus

    if (!Array.isArray(menus)) {
        return []
    }

    //移动端时，侧边栏只会按侧边栏导航模式渲染
    if (appGetters.isMobile) {
        return menus
    }

    switch (appGetters.navMode) {
        case 'aside':
            return menus
        case 'head':
            return []
        case 'aside-two-part':
        case 'mix':
            const root = menus.find(i => i.path === appGetters.activeRootMenu)
            return root ? root.children || [] : []
        default:
            return []
    }
}

//根据路由获取当前激活的菜单
export function getActiveMenuByRoute({path, meta}) {
    return meta.activeMenu || path
}

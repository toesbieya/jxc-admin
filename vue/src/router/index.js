/*
* 路由配置
*
* 需要鉴权的路由：!meta.noAuth
* 左侧菜单显示：name && !meta.hidden && meta.title
* 左侧菜单排序：能在左侧菜单中显示 && meta.sort，升序排列
* 左侧菜单不折叠只有一个children的路由：meta.alwaysShow
* 面包屑显示：meta.breadcrumb !== false && (meta.title || meta.dynamicTitle)
* tab栏显示：meta.title || meta.dynamicTitle
* tab栏固定显示：meta.title && meta.affix
* 页面不缓存：!name && !meta.usePathKey && !meta.useFullPathKey || meta.noCache
* 打开iframe：meta.iframe，不会重复打开相同src的iframe
* */
import Vue from 'vue'
import Router from 'vue-router'
import {route as routeConfig} from '@/config'
import {stringifyRoutes, parseRoutes, generateRoutes} from './util'
import defaultRoutes from './define'
import registerGuard from './guard'

Vue.use(Router)

const router = new Router({
    base: process.env.BASE_URL,
    mode: routeConfig.mode,
    scrollBehavior: () => ({y: 0}),
    routes: defaultRoutes
})

registerGuard(router)

export default router

export function addDynamicRoutes(jsonTree) {
    jsonTree = parseRoutes(stringifyRoutes(jsonTree))
    const endRoute = {path: '*', redirect: '/404'}
    router.addRoutes([...generateRoutes(jsonTree), endRoute])
}

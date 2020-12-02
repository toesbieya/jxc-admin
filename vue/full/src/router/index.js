/**
 * 路由配置说明
 *
 * 不需要鉴权的路由：meta.noAuth
 * 左侧菜单显示：name && !meta.hidden && meta.title
 * 左侧菜单排序：能在左侧菜单中显示 && meta.sort，升序排列
 * 左侧菜单不折叠只有一个children的路由：meta.alwaysShow
 * 页头显示：meta.pageHeader !== false && (meta.title || meta.dynamicTitle)
 * tab栏显示：meta.title || meta.dynamicTitle
 * tab栏固定显示：meta.title && meta.affix
 * 页面不缓存：!name && !meta.usePathKey && !meta.useFullPathKey || meta.noCache
 * 打开iframe：meta.iframe，不会重复打开相同src的iframe
 */

import Vue from 'vue'
import Router from 'vue-router'
import {route as routeConfig} from '@/config'
import {deepClone} from "@/util"
import {generateRoutes} from './util'
import defaultRoutes from './define'
import registerGuardian from './guardian'

Vue.use(Router)

const router = new Router({
    base: process.env.BASE_URL,
    mode: routeConfig.mode,
    scrollBehavior: () => ({y: 0}),
    routes: defaultRoutes
})

registerGuardian(router)

export default router

/**
 * 向外部暴露的动态路由添加方法，只能被调用一次
 *
 * @param jsonTree {array} 路由配置项构成的树型数组
 */
export const addDynamicRoutes = (() => {
    let init = false

    return (jsonTree) => {
        if (init) return
        init = true

        jsonTree = deepClone(jsonTree)
        const endRoute = {path: '*', redirect: '/404'}
        router.addRoutes([...generateRoutes(jsonTree), endRoute])
    }
})()

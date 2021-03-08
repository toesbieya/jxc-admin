/**
 * 路由配置说明
 *
 * 不需要鉴权的路由：meta.noAuth
 * 更多请查看[el-admin-layout](https://doc.toesbieya.cn/el-admin-layout/api/)
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

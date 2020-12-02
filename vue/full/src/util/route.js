/**
 * 路由控制工具类
 */

import {isEmpty} from "@/util"
import {isString} from "@/util/validate"
import router from '@/router'
import {mutations as tagsViewMutations} from "@/layout/store/tagsView"

/**
 * 路由刷新
 *
 * @param route {string|route} 需要刷新的路由，不传时为当前路由，如果是字符串时请确保以'/'开头
 * @param replace {boolean}    是否使用replace进行跳转
 * @return {Promise}          返回vue-router跳转的结果
 */
export function refreshPage(route = router.currentRoute, replace = true) {
    const target = `/redirect${isString(route) ? route : route.fullPath}`
    return router[replace ? 'replace' : 'push'](target)
}

/**
 * 关闭当前页，如果传入next则跳转到next页面
 *
 * @param next {string|route}   跳转的目标页面，作为第一个参数传入vue-router.replace
 * @return {undefined|Promise}  仅在next有值时，返回vue-router.replace的结果
 */
export function closeCurrentPage(next) {
    tagsViewMutations.delTagAndCache(router.currentRoute)
    if (!isEmpty(next)) {
        return router.replace(next)
    }
}

/**
 * 路由控制工具类
 */
import router from "@/router"
import {refreshPage as refreshLayoutPage, closeCurrentPage as closeLayoutCurrentPage} from "el-admin-layout/src/helper"

/**
 * 路由刷新
 *
 * @param route               需要刷新的路由对象，不传时为当前路由
 * @param replace {boolean}   是否使用replace进行跳转
 * @return {Promise}          返回vue-router跳转的结果
 */
export function refreshPage(route = router.currentRoute, replace) {
    return refreshLayoutPage(route, router, replace)
}

/**
 * 关闭当前页，如果传入next则跳转到next页面
 *
 * @param next {string|route}   跳转的目标页面，作为第一个参数传入vue-router.replace
 * @return {undefined|Promise}  仅在next有值时，返回vue-router.replace的结果
 */
export function closeCurrentPage(next) {
    return closeLayoutCurrentPage(router, next)
}

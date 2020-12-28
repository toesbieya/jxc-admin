/**
 * 路由控制工具类
 */
import router from "@/router"
import {refreshPage as refreshLayoutPage, closeCurrentPage} from "el-admin-layout/src/util"

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

export {closeCurrentPage}

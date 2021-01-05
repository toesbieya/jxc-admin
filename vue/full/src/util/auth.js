import {Const} from 'el-admin-layout'
import store from '@/store'
import {uppercaseFirst} from "@/filter"
import {isEmpty} from "@/util"

/**
 * 判断是否需要鉴权，需要则返回true
 * 以下情况不需要鉴权：①无meta、②是redirect、③没有名称、④设置了noAuth=true
 *
 * @param route vue-router的routeConfig或项目中说明的路由配置项
 * @return {boolean}
 */
export function needAuth(route) {
    const {path, meta} = route

    if (!meta || path.startsWith(Const.redirectPath)) return false

    if (isEmpty(meta.title) && isEmpty(meta.dynamicTitle)) return false

    return meta.noAuth !== true
}

/**
 * 接口权限判断，有权限则返回true
 * 调用时需要保证用户已经登录成功
 *
 * @param path {string}  接口路径
 * @return {boolean}
 */
export function auth(path) {
    if (store.state.user.admin === true) return true

    const resourceMap = store.state.resource.resourceMap
    if (!(path in resourceMap)) return true

    const resources = store.state.user.resources
    return resources && path in resources
}

/**
 * what i can
 * 批量生成canXxx的计算属性
 *
 * @param apiMap {object}  <key:接口名称，value:接口对象>
 * @return {object}
 */
export function wic(apiMap) {
    const attrs = {}

    Object.entries(apiMap).forEach(([key, value]) => {
        const attrKey = `can${uppercaseFirst(key)}`
        attrs[attrKey] = () => auth(value.url)
    })

    return attrs
}

/**
 * 判断是否已登录，已登录则返回true
 *
 * @return {boolean}
 */
export function isLogin() {
    return !isEmpty(store.state.user.token)
}

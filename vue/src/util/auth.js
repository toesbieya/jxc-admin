import store from '@/store'
import {uppercaseFirst} from "@/filter"
import {isEmpty} from "@/util"

/**
 * @desc 判断是否需要鉴权，需要则返回true
 * @param route vue-router的routeConfig或项目中说明的路由配置项
 * @returns {boolean}
 */
export function needAuth(route) {
    if (route.path.startsWith('/redirect')) return false
    const {meta} = route
    return !meta || !meta.noAuth || !isEmpty(meta.title, meta.dynamicTitle)
}

//接口权限判断，有权限则返回true
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
 * @param apiMap  <key:接口名称，value:接口对象>
 */
export function wic(apiMap) {
    const attrs = {}

    Object.entries(apiMap).forEach(([key, value]) => {
        const attrKey = `can${uppercaseFirst(key)}`
        attrs[attrKey] = () => auth(value.url)
    })

    return attrs
}

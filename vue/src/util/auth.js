import store from '@/store'
import {isEmpty} from "@/util/index"

//判断路由是否需要鉴权
export function needAuth(route) {
    if (route.path.startsWith('/redirect')) return false
    const {meta} = route
    return !meta || !meta.noAuth || isEmpty(meta.title) && isEmpty(meta.dynamicTitle)
}

//接口权限判断，有权限则返回true
export function auth(path) {
    if (store.state.user.admin === true) return true

    const resourceMap = store.state.resource.dataMap
    if (!(path in resourceMap)) return true

    const resources = store.state.user.resources
    return resources && path in resources
}

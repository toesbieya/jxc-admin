import store from '@/store'

export function needAuth(route) {
    if (route.path.startsWith('/redirect')) return false
    return !(route.meta && (!('noAuth' in route) || route.meta.noAuth))
}

export function auth(path) {
    if (store.state.user.admin === 1) return true

    const resourceMap = store.state.resource.dataMap
    if (!(path in resourceMap)) return true

    const resources = store.state.user.resources
    return resources && path in resources
}

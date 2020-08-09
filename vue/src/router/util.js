import {pathToRegexp} from 'path-to-regexp'
import PageSkeleton from "@/component/Skeleton/PageSkeleton"
import {title} from "@/config"
import store from "@/store"
import {isEmpty} from "@/util"

//拼接页面标题
export function setPageTitle(route) {
    const pageTitle = route.meta.title
    document.title = pageTitle ? `${pageTitle} - ${title}` : title
}

//确定路由的标题
export function specifyRouteTitle(to, from) {
    const {meta} = to
    if (typeof meta.dynamicTitle === 'function') {
        meta.title = meta.dynamicTitle(to, from)
    }
}

//将给定的白名单url转换为正则
export function transformWhiteList(list) {
    return list.map(pathToRegexp)
}

//子路由继承父路由meta上的{noAuth,noCache}，优先使用子路由的值
export function metaExtend(routes, parentMeta) {
    const keys = ['noAuth', 'noCache']
    routes.forEach(route => {
        if (parentMeta) {
            Object.keys(parentMeta).forEach(key => {
                const value = parentMeta[key]
                if (keys.includes(key) && !isEmpty(value)) {
                    if (!route.meta) {
                        route.meta = {[key]: value}
                    }
                    else if (isEmpty(route.meta[key])) {
                        route.meta[key] = value
                    }
                }
            })
        }
        route.children && metaExtend(route.children, route.meta)
    })
}

//路由页面懒加载
export function lazyLoadView(path) {
    if (path.endsWith('/')) path += 'index'
    else path = path.replace('Page.vue', '')

    const AsyncHandler = () => ({component: import(`@/view/${path}Page.vue`), loading: PageSkeleton})
    return () => Promise.resolve({
        functional: true,
        render(h, {data, children}) {
            return h(AsyncHandler, data, children)
        }
    })
}

//判断是否需要一次额外的redirect跳转
export function needExtraRedirect(to, from) {
    //若是共用组件的路由页面之间的跳转，借助redirect避免组件复用
    const a = to.meta.commonModule, b = from.meta.commonModule
    return !isEmpty(a) && a === b
}

//初始化菜单和权限
export function initMenuAndResource() {
    if (!store.state.resource.init) {
        return store.dispatch('resource/init', store.state.user)
    }
    return Promise.resolve()
}

//判断是否需要打开iframe
export function iframeControl(to, from) {
    let iframe = to.meta.iframe
    const operate = iframe ? 'open' : 'close'
    if (to.path === `/redirect${from.path}`) {
        iframe = from.meta.iframe
    }
    return store.dispatch(`iframe/${operate}`, iframe)
}

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
    if (typeof to.meta.dynamicTitle === 'function') {
        to.meta.title = to.meta.dynamicTitle(to, from)
    }
}

//将给定的白名单url转换为正则
export function transformWhiteList(list) {
    return list.map(url => pathToRegexp(url))
}

//子路由继承父路由meta上的{affix,noAuth,noCache}，优先使用子路由的值
export function metaExtend(routes, meta) {
    routes.forEach(route => {
        if (meta) {
            const keys = ['affix', 'noAuth', 'noCache']
            Object.keys(meta).forEach(key => {
                if (keys.includes(key) && !isEmpty(meta[key]) && isEmpty(route.meta[key])) {
                    route.meta[key] = meta[key]
                }
            })
        }
        if (route.children) {
            metaExtend(route.children, route.meta)
        }
    })
}

//路由页面懒加载
export function lazyLoadView(component) {
    const AsyncHandler = () => ({component, loading: PageSkeleton})
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
    if (!store.state.resource.hasInitRoutes) {
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

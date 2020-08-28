/*
* 路由配置
*
* 需要鉴权的路由：!meta.noAuth
* 左侧菜单显示：name && !meta.hidden && meta.title
* 左侧菜单排序：能在左侧菜单中显示 && meta.sort，升序排列
* 左侧菜单不折叠只有一个children的路由：meta.alwaysShow
* 面包屑显示：meta.title || meta.dynamicTitle
* tab栏显示：meta.title || meta.dynamicTitle
* tab栏固定显示：meta.title && meta.affix
* 页面不缓存：!name && !meta.usePathKey && !meta.useFullPathKey || meta.noCache
* 打开iframe：meta.iframe，不会重复打开相同src的iframe
* */
import Vue from 'vue'
import Router from 'vue-router'
import {pathToRegexp} from 'path-to-regexp'
import NProgress from 'nprogress'
import {route as routeConfig, title} from '@/config'
import defaultRoutes from '@/router/define'
import store from "@/store"
import {stringifyRoutes, parseRoutes, generateRoutes} from './util'
import {isEmpty} from "@/util"
import {auth, needAuth} from "@/util/auth"
import {isUserExist} from "@/util/storage"

Vue.use(Router)

NProgress.configure({showSpinner: false})

const whiteList = transformWhiteList(['/login', '/register', '/403', '/404', '/500'])

const router = new Router({
    base: process.env.BASE_URL,
    mode: routeConfig.mode,
    scrollBehavior: () => ({y: 0}),
    routes: defaultRoutes
})

router.beforeEach(async (to, from, next) => {
    //使用redirect进行跳转时不显示进度条
    !to.path.startsWith('/redirect') && NProgress.start()

    if (needExtraRedirect(to, from)) {
        //这里vue-router会报redirect错误，已在main.js中忽略
        return next(`/redirect${to.fullPath}`)
    }

    specifyRouteTitle(to, from)

    setPageTitle(to)

    //白名单内不需要进行权限控制
    if (whiteList.some(reg => reg.test(to.path))) {
        return next()
    }

    //未登录时返回登录页
    if (!isUserExist()) {
        return next({path: '/login', query: {redirect: to.fullPath}})
    }

    setActiveRootMenu(to)

    //初始化路由和菜单权限
    if (!store.state.resource.init) {
        await store.dispatch('resource/init', {...store.state.user, addRoutes: true})
        return next({...to, replace: true})
    }

    //页面不需要鉴权或有访问权限时通过
    if (!needAuth(to) || auth(getAuthorizedPath(to))) {
        iframeControl(to, from)
        return next()
    }

    //用户无权限访问时的动作
    next('/403')
})

router.afterEach(() => NProgress.done())

//将给定的白名单url转换为正则
function transformWhiteList(list) {
    return list.map(pathToRegexp)
}

//拼接页面标题
function setPageTitle(route) {
    const pageTitle = route.meta.title
    document.title = pageTitle ? `${pageTitle} - ${title}` : title
}

//确定路由的标题
function specifyRouteTitle(to, from) {
    const {meta} = to
    if (typeof meta.dynamicTitle === 'function') {
        meta.title = meta.dynamicTitle(to, from)
    }
}

//判断是否需要一次额外的redirect跳转
function needExtraRedirect(to, from) {
    //若是共用组件的路由页面之间的跳转，借助redirect避免组件复用
    const a = to.meta.commonModule, b = from.meta.commonModule
    return !isEmpty(a) && a === b
}

//设置顶部菜单的激活状态
function setActiveRootMenu(to) {
    const {path, matched} = to
    //使用/redirect跳转 或 无匹配路由 时跳过
    if (path.startsWith('/redirect') || matched.length === 0) {
        return
    }
    store.commit('resource/activeRootMenu', matched[0].path || '/')
}

//获取进行权限验证的路由地址，使用了动态路由的会用到
function getAuthorizedPath(route) {
    const {params, path, matched} = route
    const isDynamic = Object.values(params).some(v => path.includes(v))
    return isDynamic ? matched[matched.length - 1].path : path
}

//判断是否需要打开iframe
function iframeControl(to, from) {
    //从iframe页面离开时，判断是否需要删除iframe
    if (from.meta.iframe) {
        let del = false
        //如果设置了无缓存或是进行了刷新，那么移除iframe
        if (from.meta.noCache || to.path === `/redirect${from.path}`) {
            del = true
        }
        store.dispatch('iframe/close', {src: from.meta.iframe, del})
    }

    //跳转至iframe页面时，打开iframe
    if (to.meta.iframe) {
        store.dispatch('iframe/open', {src: to.meta.iframe})
    }
}

export default router

export function addDynamicRoutes(json) {
    json = parseRoutes(stringifyRoutes(json))
    const endRoute = {path: '*', redirect: '/404'}
    router.addRoutes([...generateRoutes(json), endRoute])
}

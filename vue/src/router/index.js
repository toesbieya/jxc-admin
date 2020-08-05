/*
* 路由配置
*
* 需要鉴权的路由：!meta.noAuth
* 左侧菜单显示：name && !hidden && meta.title
* 左侧菜单排序：能在左侧菜单中显示 && sort，升序排列
* 左侧菜单不折叠只有一个children的路由：alwaysShow
* 面包屑显示：meta.title || meta.dynamicTitle
* 搜索选项显示：name && meta.title
* tab栏显示：name && (meta.title || meta.dynamicTitle)
* tab栏固定显示：meta.affix
* 页面不缓存：!name && !meta.isDetailPage || meta.noCache
* 打开iframe：meta.iframe，不会重复打开相同src的iframe
* */
import Vue from 'vue'
import Router from 'vue-router'
import store from "@/store"
import NProgress from 'nprogress'
import {isUserExist} from "@/util/storage"
import {auth, needAuth} from "@/util/auth"
import {setPageTitle, specifyRouteTitle, transformWhiteList, metaExtend} from './util'
import {contextPath, routerMode} from '@/config'
import constantRoutes from '@/router/constant'
import authorityRoutes from '@/router/authority'

Vue.use(Router)

NProgress.configure({showSpinner: false})

metaExtend(constantRoutes)
metaExtend(authorityRoutes)

const endRoute = [{path: '*', redirect: '/404', hidden: true}]

const whiteList = transformWhiteList(['/login', '/register', '/404', '/403'])

const router = new Router({
    base: contextPath,
    mode: routerMode,
    scrollBehavior: () => ({y: 0}),
    routes: constantRoutes.concat(authorityRoutes, endRoute)
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
    if (whiteList.some(reg => reg.test(to.path))) return next()

    //未登录时返回登录页
    if (!isUserExist()) return next({path: '/login', query: {redirect: to.fullPath}})

    await initMenuAndResource()

    //页面不需要鉴权或有访问权限时通过
    if (!needAuth(to) || auth(to.path)) {
        iframeControl(to, from)
        return next()
    }

    //用户无权限访问时的动作
    next('/403')
})

router.afterEach(() => NProgress.done())

//判断是否需要一次额外的redirect跳转
function needExtraRedirect(to, from) {
    //若是详情页之间的跳转，借助redirect避免组件复用
    const isToDetailPage = to.meta.isDetailPage,
        isFromDetailPage = from.meta.isDetailPage

    return isToDetailPage !== undefined && isToDetailPage === isFromDetailPage
}

//初始化菜单和权限
function initMenuAndResource() {
    if (!store.state.resource.hasInitRoutes) {
        return store.dispatch('resource/init', store.state.user)
    }
    return Promise.resolve()
}

//判断是否需要打开iframe
function iframeControl(to, from) {
    let iframe = to.meta.iframe
    const operate = iframe ? 'open' : 'close'
    if (to.path === `/redirect${from.path}`) {
        iframe = from.meta.iframe
    }
    return store.dispatch(`iframe/${operate}`, iframe)
}

export default router

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
import NProgress from 'nprogress'
import {contextPath, routerMode} from '@/config'
import constantRoutes from '@/router/constant'
import authorityRoutes from '@/router/authority'
import {auth, needAuth} from "@/util/auth"
import {isUserExist} from "@/util/storage"
import {
    setPageTitle,
    specifyRouteTitle,
    transformWhiteList,
    metaExtend,
    needExtraRedirect,
    initMenuAndResource,
    iframeControl
} from './util'

Vue.use(Router)

NProgress.configure({showSpinner: false})

metaExtend(constantRoutes)
metaExtend(authorityRoutes)

const endRoute = [{path: '*', redirect: '/404', meta: {hidden: true}}]

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

export default router

import {pathToRegexp} from 'path-to-regexp'
import store from "@/store"
import {auth, needAuth} from "@/util/auth"
import {isUserExist} from "@/util/storage"

const whiteList = ['/login', '/register', '/403', '/404', '/500'].map(url => pathToRegexp(url))

const beforeEach = async (to, from, next) => {
    //白名单内不需要进行权限控制
    if (whiteList.some(reg => reg.test(to.path))) {
        return next()
    }

    //未登录时返回登录页
    if (!isUserExist()) {
        return next({path: '/login', query: {redirect: to.fullPath}})
    }

    //初始化路由和菜单权限
    if (!store.state.resource.init) {
        await store.dispatch('resource/init', store.state.user)
        return next({...to, replace: true})
    }

    //页面不需要鉴权或有访问权限时通过
    if (!needAuth(to) || auth(getAuthorizedPath(to))) {
        return next()
    }

    //用户无权限访问时的动作
    next('/403')
}

//获取进行权限验证的路由地址，使用了动态路由的会用到
function getAuthorizedPath(route) {
    const {params, path, matched} = route
    const isDynamic = Object.values(params).some(v => path.includes(v))
    return isDynamic ? matched[matched.length - 1].path : path
}

export default function (router) {
    router.beforeEach(beforeEach)
}

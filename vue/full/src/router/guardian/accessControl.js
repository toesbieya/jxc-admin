import store from "@/store"
import {isLogin, auth, needAuth} from "@/util/auth"

const noLoginList = ['/login', '/register', '/403', '/404', '/500']

const beforeEach = async (to, from, next) => {
    //放行不需要登录即可访问的路由
    if (noLoginList.includes(to.path)) {
        return next()
    }

    //未登录时返回登录页
    if (!isLogin()) {
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

//获取进行权限验证的路由地址
function getAuthorizedPath(route) {
    const {path, matched} = route, len = matched.length

    //可能会有路由使用动态路径(诸如/:id)，matched中的path是routeConfig中声明的原始路径
    return len > 0 ? matched[len - 1].path : path
}

export default function (router) {
    router.beforeEach(beforeEach)
}

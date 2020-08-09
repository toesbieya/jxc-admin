import path from 'path'
import {dynamicRoutes} from '@/router/define'
import {needAuth} from "@/util/auth"
import {createTree} from "@/util/tree"
import {getAllResources} from "@/api/system/resource"
import {isEmpty} from "@/util"
import {isExternal} from "@/util/validate"

const finalRoutes = transformOriginRoutes(dynamicRoutes)
clean(finalRoutes, false)

const state = {
    routes: [],
    sidebarMenus: [],
    dataMap: {},
    tree: [],
    init: false
}

const mutations = {
    routes(state, routes) {
        clean(routes)
        state.routes = routes

        const sidebarMenus = routes
        sort(sidebarMenus)
        state.sidebarMenus = sidebarMenus
    },
    data(state, data) {
        data = data || []
        state.dataMap = data.reduce((map, item) => {
            map[item.url] = item.id
            return map
        }, {})
        state.tree = createTree(data.filter(resource => resource.admin === false))
    },
    setInit(state, sign) {
        state.init = sign
    }
}

const actions = {
    init({dispatch}, user) {
        return Promise.all([
            dispatch('initRoutes', user),
            dispatch('initResource')
        ])
    },
    initRoutes({commit}, user) {
        const {resources, admin} = user
        return new Promise(resolve => {
            const accessedRoutes = getAuthorizedRoutes({resources, admin})
            commit('routes', accessedRoutes)
            commit('setInit', true)
            resolve()
        })
    },
    initResource({commit}) {
        return new Promise(resolve => {
            getAllResources()
                .then(data => {
                    commit('data', data)
                    resolve()
                })
        })
    }
}

//在原始路由数组基础上添加全路径
function transformOriginRoutes(routes) {
    const res = JSON.parse(JSON.stringify(routes))
    addFullPath(res)
    return res
}

//删除不显示的路由(没有children且没有meta.title，左侧菜单需清除meta.hidden=true)
function clean(routes, cleanHidden = true) {
    for (let i = routes.length - 1; i >= 0; i--) {
        const {children, meta: {title, alwaysShow, hidden} = {}} = routes[i]

        if (cleanHidden && hidden) {
            routes.splice(i, 1)
            continue
        }
        if (!children && !title) {
            routes.splice(i, 1)
            continue
        }
        if (children) {
            clean(children, cleanHidden)
            if (children.length < 1 && !alwaysShow) {
                routes.splice(i, 1)
            }
        }
    }
}

//路由添加全路径
function addFullPath(routes, basePath = '/') {
    routes.forEach(route => {
        delete route.component
        route.fullPath = isExternal(route.path) ? route.path : path.resolve(basePath, route.path)
        route.children && addFullPath(route.children, route.fullPath)
    })
}

//获取经过权限控制后的路由
function getAuthorizedRoutes({resources, admin}) {
    if (admin === true) return finalRoutes
    if (!resources) return []
    filter(finalRoutes, i => !needAuth(i) || i.fullPath in resources)
    return finalRoutes
}

//若没有children且未通过，则删除，若有，当children长度为0时删除
function filter(arr, fun) {
    for (let i = arr.length - 1; i >= 0; i--) {
        const {children} = arr[i]

        if (!children) {
            !fun(arr[i]) && arr.splice(i, 1)
            continue
        }

        filter(children, fun)

        children.length <= 0 && arr.splice(i, 1)
    }
}

//菜单排序
function sort(routes) {
    routes.sort((pre, next) => {
        pre = getSortValue(pre)
        next = getSortValue(next)
        if (pre < next) return -1
        else if (pre === next) return 0
        else return 1
    })
    routes.forEach(route => {
        const {children} = route
        if (children && children.length) {
            sort(children)
        }
    })
}

const getSortValue = item => {
    const sort = deepTap(item)
    return isEmpty(sort) ? 10000 : sort
}
const deepTap = item => {
    const {name, children = [], meta: {title, hidden, sort} = {}} = item
    if (hidden) return null
    if (!isEmpty(sort)) return sort
    //如果是类似首页那样的路由层级
    if (isEmpty(name, title) && children.length === 1) {
        return deepTap(children[0])
    }
    return null
}

export default {
    namespaced: true,
    state,
    mutations,
    actions
}

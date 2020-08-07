import path from 'path'
import constantRoutes from '@/router/constant'
import authorityRoutes from '@/router/authority'
import {needAuth} from "@/util/auth"
import {createTree} from "@/util/tree"
import {getAllResources} from "@/api/system/resource"
import {isEmpty} from "@/util"
import {isExternal} from "@/util/validate"

const finalConstantRoutes = transformOriginRoutes(constantRoutes)
const finalAuthorityRoutes = transformOriginRoutes(authorityRoutes)
clean(finalConstantRoutes, false)
clean(finalAuthorityRoutes, false)

const state = {
    routes: [],
    sidebarMenus: [],
    dataMap: {},
    tree: [],
    hasInitRoutes: false
}

const mutations = {
    routes(state, routes) {
        const tempConstantRoutes = JSON.parse(JSON.stringify(finalConstantRoutes))
        const tempAuthorityRoutes = JSON.parse(JSON.stringify(routes))

        clean(tempConstantRoutes)
        clean(tempAuthorityRoutes)

        state.routes = finalConstantRoutes.concat(routes)

        const sidebarMenus = tempConstantRoutes.concat(tempAuthorityRoutes)
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
    hasInitRoutes(state, sign) {
        state.hasInitRoutes = sign
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
            commit('hasInitRoutes', true)
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
        delete route.components
        route.fullPath = isExternal(route.path) ? route.path : path.resolve(basePath, route.path)
        route.children && addFullPath(route.children, route.fullPath)
    })
}

//获取经过权限控制后的路由
function getAuthorizedRoutes({resources, admin}) {
    if (admin === true) return finalAuthorityRoutes
    if (!resources) return []
    const arr = JSON.parse(JSON.stringify(finalAuthorityRoutes))
    filter(arr, i => !needAuth(i) || i.fullPath in resources)
    return arr
}

//若没有children且未通过，则删除，若有，当children长度为0时删除
function filter(arr, fun) {
    for (let i = 0; i < arr.length; i++) {
        if (!arr[i].children && !fun(arr[i])) {
            arr.splice(i, 1)
            i--
            continue
        }

        if (!arr[i].children) continue

        filter(arr[i].children, fun)

        if (arr[i].children.length <= 0) {
            arr.splice(i, 1)
            i--
        }
    }
}

//菜单排序
function sort(routes, getSortValue = defaultGetSortValue) {
    routes.sort((pre, next) => {
        const preSort = getSortValue(pre),
            nextSort = getSortValue(next)
        if (preSort < nextSort) return -1
        else if (preSort === nextSort) return 0
        else return 1
    })
    routes.forEach(route => {
        if (route.children && route.children.length > 1) {
            sort(route.children, getSortValue)
        }
    })
}

const defaultGetSortValue = item => {
    const {meta: {sort} = {}} = deepTap(item) || {}
    return isEmpty(sort) ? 10000 : sort
}

const deepTap = item => {
    const {name, children, meta: {title, hidden, sort} = {}} = item
    if (hidden) return null
    if (!isEmpty(sort)) return item
    if (isEmpty(name, title)) {
        //如果是类似首页那样的路由层级
        if (children && children.length === 1) {
            return deepTap(children[0])
        }
    }
    return null
}

export default {
    namespaced: true,
    state,
    mutations,
    actions
}

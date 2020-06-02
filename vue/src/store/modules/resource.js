import path from 'path'
import constantRoutes from '@/router/constantRoutes'
import authorityRoutes from '@/router/authorityRoutes'
import {needAuth} from "@/utils/auth"
import {createTree} from "@/utils/tree"
import {getLocalResource, setLocalResource} from "@/utils/localStorage"
import {getResources} from "@/api/system/resource"

const finalConstantRoutes = transformOriginRoutes(constantRoutes)
const finalAuthorityRoutes = transformOriginRoutes(authorityRoutes)
clean(finalConstantRoutes, false)
clean(finalAuthorityRoutes, false)

const localResource = getLocalResource()

const state = {
    routes: [],
    sidebarMenus: [],
    data: localResource,
    dataMap: {},
    tree: createTree(localResource),
    hasInitRoutes: false
}

const mutations = {
    routes(state, routes) {
        let tempConstantRoutes = JSON.parse(JSON.stringify(finalConstantRoutes))
        let tempAuthorityRoutes = JSON.parse(JSON.stringify(routes))
        clean(tempConstantRoutes)
        clean(tempAuthorityRoutes)
        state.routes = finalConstantRoutes.concat(routes)
        state.sidebarMenus = tempConstantRoutes.concat(tempAuthorityRoutes)
    },
    data(state, data) {
        state.data = data || []
        state.dataMap = state.data.reduce((map, item) => {
            map[item.url] = item.id
            return map
        }, {})
        state.tree = createTree(state.data)
        setLocalResource(data)
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
            let accessedRoutes = getAuthorizedRoutes({resources, admin})
            commit('routes', accessedRoutes)
            commit('hasInitRoutes', true)
            resolve()
        })
    },
    initResource({commit}) {
        return new Promise(resolve => {
            //if (state.data.length > 0) return resolve();
            getResources()
                .then(data => {
                    commit('data', data)
                    resolve()
                })
        })
    }
}

//在原始路由数组基础上添加全路径
function transformOriginRoutes(routes) {
    let res = JSON.parse(JSON.stringify(routes))

    addFullPath(res)

    return res
}

//删除不显示的路由(没有children且没有meta.title，左侧菜单需清除hidden=true)
function clean(routes, cleanHidden = true) {
    for (let i = routes.length - 1; i >= 0; i--) {
        if (cleanHidden && routes[i].hidden) {
            routes.splice(i, 1)
            continue
        }
        if (!routes[i].children && (!routes[i].meta || !routes[i].meta.title)) {
            routes.splice(i, 1)
            continue
        }
        if (routes[i].children) {
            clean(routes[i].children, cleanHidden)
            if (routes[i].children.length < 1 && routes[i].alwaysShow !== true) {
                routes.splice(i, 1)
            }
        }
    }
}

//路由添加全路径
function addFullPath(routes, basePath = '/') {
    routes.forEach(route => {
        delete route.components
        route.fullPath = path.resolve(basePath, route.path)
        route.children && addFullPath(route.children, route.fullPath)
    })
}

//获取经过权限控制后的路由
function getAuthorizedRoutes({resources, admin}) {
    if (admin === 1) return finalAuthorityRoutes
    if (!resources) return []
    let arr = JSON.parse(JSON.stringify(finalAuthorityRoutes))
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

export default {
    namespaced: true,
    state,
    mutations,
    actions
}

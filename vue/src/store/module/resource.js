import path from 'path'
import {route as routeConfig} from '@/config'
import {mutations as appMutations} from "@/layout/store/app"
import {addDynamicRoutes} from '@/router'
import {getDynamicRoutes} from '@/router/define'
import {parseRoutes, metaExtend} from "@/router/util"
import {getAll} from "@/api/system/resource"
import {isEmpty} from "@/util"
import {needAuth} from "@/util/auth"
import {createTree} from "@/util/tree"
import {isExternal} from "@/util/validate"

const state = {
    //后端返回的原始权限数据
    //权限表：<权限路径，权限id>，用于util.auth
    resourceMap: {},
    //由原始权限数据生成的树（当用户非admin时过滤了admin专属权限）
    resourceTree: [],

    //判断权限是否已经初始化
    init: false
}

const mutations = {
    resource(state, {data, admin}) {
        state.resourceMap = generateResourceMap(data)

        //如果不是admin用户，那么过滤admin类型的权限
        if (!admin) data = data.filter(i => !i.admin)

        //解析meta，这里不处理meta.dynamicTitle
        data.forEach(i => {
            if (typeof i.meta === 'string' && !isEmpty(i.meta)) {
                i.meta = JSON.parse(i.meta)
            }
        })

        state.resourceTree = createTree(data)
    },
    init(state, sign) {
        state.init = sign
    }
}

const actions = {
    init({commit}, {resources, admin, addRoutes = false}) {
        return getAll
            .request()
            .then(({data}) => {
                //动态添加路由，这里不需要进行权限过滤
                const routes = transformOriginRouteData(data)
                metaExtend(routes)
                addRoutes && addDynamicRoutes(routes)

                //获取经过权限过滤后的菜单
                const menus = getAuthorizedMenus({resources, admin}, routes)

                appMutations.menus(menus)
                commit('resource', {data: data || [], admin})

                //设置初始化完成的标志
                commit('init', true)
            })
    }
}

//将后台返回的数据转换为合法的route数组
function transformOriginRouteData(data) {
    if (!Array.isArray(data)) return []
    if (!routeConfig.useBackendDataAsRoute) return getDynamicRoutes()
    data = JSON.parse(JSON.stringify(data))
    data = data.filter(i => {
        if (i.type === 3 || !i.enable) {
            return false
        }
        ['name', 'component', 'meta'].forEach(key => {
            if (key in i && isEmpty(i[key])) {
                delete i[key]
            }
        })
        if (typeof i.meta === 'string') {
            i.meta = parseRoutes(i.meta)
        }
        return true
    })
    return createTree(data)
}

//获取经过权限控制后的菜单
function getAuthorizedMenus({resources, admin}, menus) {
    menus = JSON.parse(JSON.stringify(menus))
    clean(menus)
    addFullPath(menus)
    if (admin === true) return menus
    if (!resources) return []
    filter(menus, i => !needAuth(i) || i.fullPath in resources)
    return menus
}

//菜单添加全路径
function addFullPath(routes, basePath = '/') {
    routes.forEach(route => {
        route.fullPath = isExternal(route.path) ? route.path : path.resolve(basePath, route.path)
        route.children && addFullPath(route.children, route.fullPath)
    })
}

//若没有children且未通过，则删除，若有，当children长度为0时删除
function filter(arr, fun) {
    for (let i = arr.length - 1; i >= 0; i--) {
        const {children} = arr[i]

        if (!children || children.length <= 0) {
            !fun(arr[i]) && arr.splice(i, 1)
            continue
        }

        filter(children, fun)

        children.length <= 0 && arr.splice(i, 1)
    }
}

//删除不显示的菜单(没有children且没有meta.title，左侧菜单需清除meta.hidden=true)
function clean(menus, cleanHidden = true) {
    for (let i = menus.length - 1; i >= 0; i--) {
        const {children = [], meta: {title, alwaysShow, hidden} = {}} = menus[i]

        if (cleanHidden && hidden) {
            menus.splice(i, 1)
            continue
        }

        if (children.length === 0) {
            if (isEmpty(title)) {
                menus.splice(i, 1)
            }
            continue
        }

        clean(children, cleanHidden)

        if (children.length === 0 && !alwaysShow) {
            menus.splice(i, 1)
        }

        //移除类似首页那样的无标题父级节点
        if (isEmpty(title)) {
            menus.splice(i, 1, ...children)
        }
    }
}

//根据权限列表生成哈希表：<权限路径，权限id>
function generateResourceMap(resources) {
    if (!Array.isArray(resources) || resources.length <= 0) {
        return {}
    }

    resources = JSON.parse(JSON.stringify(resources))

    const result = {}

    //临时表：<id,resource>
    const map = resources.reduce((map, i) => {
        //如果是接口类型的节点，直接放入结果表
        if (i.type === 3) result[i.path] = i.id
        else {
            i.children = []
            map[i.id] = i
        }
        return map
    }, {})

    //获取所有的菜单叶子节点
    const menuLeaves = resources.filter(node => {
        const parent = map[node.pid]
        if (parent) {
            parent.children.push(node)
            node.parent = parent
        }
        return node.type === 2
    })
    //获取叶子菜单的全路径
    const getMenuFullPath = menu => {
        const parents = [menu.path]
        let {parent} = menu
        while (parent) {
            parents.push(parent.path)
            parent = parent.parent
        }
        return path.resolve(...parents.reverse())
    }

    //填充结果表
    for (const menu of menuLeaves) {
        result[getMenuFullPath(menu)] = menu.id
    }

    return result
}

export default {
    namespaced: true,
    state,
    mutations,
    actions
}

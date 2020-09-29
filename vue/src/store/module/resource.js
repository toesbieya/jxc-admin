import path from 'path'
import {route as routeConfig} from '@/config'
import {mutations as appMutations} from "@/layout/store/app"
import {addDynamicRoutes} from '@/router'
import {getDynamicRoutes} from '@/router/define'
import {parseRoutes, metaExtend} from "@/router/util"
import {getAll} from "@/api/system/resource"
import {isEmpty} from "@/util"
import {needAuth, auth} from "@/util/auth" // 此处存在循环引用？
import {createTree} from "@/util/tree"
import {isExternal} from "@/util/validate"

const state = {
    //权限映射表：<权限路径，权限id>，用于util.auth
    resourceMap: {},
    //权限树（当用户非admin时过滤了admin专属权限），用于菜单管理、角色管理中的权限选择
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
    init({state, commit}, {resources, admin}) {
        return getAll
            .request()
            .then(({data}) => {
                commit('resource', {data: data || [], admin})

                //动态添加路由，这里不需要进行权限过滤
                const routes = transformOriginRouteData(data)
                metaExtend(routes)
                //可能存在多次调用的情况，所以仅在第一次调用时添加进vue-router
                !state.init && addDynamicRoutes(routes)

                //生成经过权限过滤后的菜单
                appMutations.menus(getAuthorizedMenus({resources, admin}, routes))

                //设置初始化完成的标志
                commit('init', true)
            })
    }
}

//将后台返回的数据转换为合法的route数组
function transformOriginRouteData(data) {
    if (!Array.isArray(data)) return []

    //未开启后端动态路由功能时，返回前端预设的静态路由
    if (!routeConfig.useBackendDataAsRoute) return getDynamicRoutes()

    data = JSON.parse(JSON.stringify(data)).filter(i => {
        //过滤掉数据接口或者未启用的项
        if (i.type === 3 || !i.enable) {
            return false
        }

        //清理一些空属性，避免vue-router出错
        ['name', 'component', 'meta'].forEach(key => {
            if (key in i && isEmpty(i[key])) {
                delete i[key]
            }
        })

        //将字符串形式的meta转为合法的js对象
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
    filter(menus, i => !needAuth(i) || auth(i.fullPath))
    return menus
}

//删除不显示的菜单(没有children且没有meta.title)
function clean(menus) {
    for (let i = menus.length - 1; i >= 0; i--) {
        const {children = [], meta: {title, alwaysShow, hidden} = {}} = menus[i]

        if (hidden) {
            menus.splice(i, 1)
            continue
        }

        if (children.length === 0) {
            if (isEmpty(title)) {
                menus.splice(i, 1)
            }
            continue
        }

        clean(children)

        if (children.length === 0 && !alwaysShow) {
            menus.splice(i, 1)
        }

        //移除类似首页那样的无标题父级节点
        if (isEmpty(title)) {
            menus.splice(i, 1, ...children)
        }
    }
}

//菜单添加全路径
function addFullPath(routes, basePath = '/') {
    routes.forEach(route => {
        //外链保持原样
        route.fullPath = isExternal(route.path) ? route.path : path.resolve(basePath, route.path)
        route.children && addFullPath(route.children, route.fullPath)
    })
}

//若没有children且传入的validate校验方法未通过，则删除，若有，当children长度为0时删除
function filter(arr, validate) {
    for (let i = arr.length - 1; i >= 0; i--) {
        const {children} = arr[i]

        if (!children || children.length <= 0) {
            !validate(arr[i]) && arr.splice(i, 1)
            continue
        }

        filter(children, validate)

        children.length <= 0 && arr.splice(i, 1)
    }
}

//根据权限列表生成哈希表：<权限路径，权限id>
function generateResourceMap(resources) {
    if (!Array.isArray(resources) || resources.length <= 0) {
        return {}
    }

    resources = JSON.parse(JSON.stringify(resources))

    const result = {}

    //用于加速查找的临时表：<id,resource>
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

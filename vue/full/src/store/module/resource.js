import {route as routeConfig} from '@/config'
import {appMutations} from "el-admin-layout"
import {addDynamicRoutes} from '@/router'
import {getDynamicRoutes} from '@/router/define'
import {str2routeConfig, metaExtend} from "@/router/util"
import {getAll} from "@/api/system/resource"
import {isEmpty, deepClone} from "@/util"
import {needAuth, auth} from "@/util/auth" //此处存在循环引用？
import {resolvePath} from "@/util/path"
import {createTree, shakeTree} from "@/util/tree"
import {isExternal} from "@/util/validate"

const state = {
    //权限映射表：<权限路径，权限id>，所有的权限数据，用于util.auth
    resourceMap: {},
    //权限树（当用户非admin时过滤了admin专属权限），用于菜单管理、角色管理中的权限选择
    resourceTree: [],

    //判断权限是否已经初始化
    init: false
}

const mutations = {
    resource(state, {data, predicate}) {
        data = deepClone(data)

        state.resourceMap = generateResourceMap(data)

        //如果传入了过滤方法，那么过滤权限树
        if (predicate) data = data.filter(predicate)

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
    init({state, commit, rootState}) {
        const user = rootState.user

        return getAll
            .request()
            .then(({data}) => {
                //如果不是admin用户，那么过滤admin类型的权限
                const predicate = user.admin ? undefined : i => !i.admin

                commit('resource', {data: data || [], predicate})

                //未开启后端动态路由功能时，使用前端预设的静态路由
                const routes =
                    routeConfig.useBackendDataAsRoute
                        ? transformOriginRouteData(data)
                        : getDynamicRoutes()

                metaExtend(routes)

                //动态添加路由，这里不需要进行权限过滤
                //可能存在多次调用的情况，所以仅在第一次调用时添加进vue-router
                !state.init && addDynamicRoutes(routes)

                //生成经过权限过滤后的菜单，并传递给layout的store
                appMutations.menus(getAuthorizedMenus(routes))

                //设置初始化完成的标志
                commit('init', true)
            })
    }
}

//将后台返回的数据转换为合法的route数组
function transformOriginRouteData(data) {
    if (!Array.isArray(data)) return []

    const treeArray = deepClone(data).filter(i => {
        //过滤掉数据接口或者未启用的项
        if (i.type === 3 || !i.enable) {
            return false
        }

        //将字符串形式的meta转为合法的js对象
        if (typeof i.meta === 'string') {
            i.meta = str2routeConfig(i.meta)
        }

        return true
    })

    return createTree(treeArray)
}

//获取经过权限控制后的菜单
function getAuthorizedMenus(menus) {
    menus = deepClone(menus)

    //删除不显示的菜单(没有children且没有meta.title)
    function clean(menus) {
        for (let i = menus.length - 1; i >= 0; i--) {
            const {children = [], meta: {title, hidden} = {}} = menus[i]

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

            if (children.length === 0) {
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
            route.fullPath = isExternal(route.path) ? route.path : resolvePath(basePath, route.path)
            route.children && addFullPath(route.children, route.fullPath)
        })
    }

    clean(menus)
    addFullPath(menus)

    return shakeTree(menus, i => {
        //非叶子节点时，当定义了children属性却没有子节点时移除
        if (i.children && i.children.length === 0) {
            return false
        }

        return !needAuth(i) || auth(i.fullPath)
    })
}

//根据权限列表生成哈希表：<权限路径，权限id>
//该方法只处理叶子节点或者是接口类型的节点
function generateResourceMap(resources) {
    if (!Array.isArray(resources) || resources.length <= 0) {
        return {}
    }

    resources = deepClone(resources)

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
        return resolvePath(...parents.reverse())
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

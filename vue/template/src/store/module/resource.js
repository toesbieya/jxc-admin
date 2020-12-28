import path from 'path'
import {appMutations} from "el-admin-layout"
import {addDynamicRoutes} from '@/router'
import {getDynamicRoutes} from '@/router/define'
import {metaExtend} from "@/router/util"
import {isEmpty, deepClone} from "@/util"
import {needAuth, auth} from "@/util/auth" //此处存在循环引用？
import {shakeTree} from "@/util/tree"
import {isExternal} from "@/util/validate"

const state = {
    //判断权限是否已经初始化
    init: false
}

const mutations = {
    init(state, sign) {
        state.init = sign
    }
}

const actions = {
    init({state, commit}) {
        return new Promise(resolve => {
            const routes = getDynamicRoutes()

            metaExtend(routes)

            //动态添加路由，这里不需要进行权限过滤
            //可能存在多次调用的情况，所以仅在第一次调用时添加进vue-router
            !state.init && addDynamicRoutes(routes)

            //生成经过权限过滤后的菜单，并传递给layout的store
            appMutations.menus(getAuthorizedMenus(routes))

            //设置初始化完成的标志
            commit('init', true)

            resolve()
        })
    }
}

//获取经过权限控制后的菜单
function getAuthorizedMenus(menus) {
    menus = deepClone(menus)

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

export default {
    namespaced: true,
    state,
    mutations,
    actions
}

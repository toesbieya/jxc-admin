import Vue from 'vue'
import {isEmpty} from "@/util"
import {isMobile} from "@/util/browser"
import {createGetters, createMutations} from "@/util/observable"
import {getLocalPersonalSettings} from "@/util/storage"

const localSettings = getLocalPersonalSettings()

const state = {
    //区分pc和移动端（mobile）
    device: isMobile() ? 'mobile' : 'pc',

    //右侧块是否含有导航栏，这里初始值是为了转为boolean类型
    hasNav: !!!localSettings.headerAutoHidden,

    //当前激活的顶部菜单的fullPath
    activeRootMenu: '',

    //所有的树形菜单，每个元素为顶部菜单，顶部菜单的子级（如果有）为侧边栏菜单
    menus: [],
}

const store = Vue.observable(state)

//菜单排序
function sort(routes) {
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

export const getters = createGetters(store)

export const mutations = {
    ...createMutations(store),

    menus(v) {
        sort(v)
        store.menus = v
    }
}

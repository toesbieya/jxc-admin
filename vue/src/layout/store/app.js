import Vue from 'vue'
import {debounce, isEmpty} from "@/util"
import {isMobile} from "@/util/browser"
import {createGetters, createMutations} from "@/util/observable"

const state = {
    //区分pc和移动端
    isMobile: isMobile(),

    //设置抽屉的显隐
    //放这里的原因是navbar可能会重新渲染，从而导致抽屉的重新渲染
    showSettingDrawer: false,

    //当前激活的顶部菜单的fullPath
    activeRootMenu: '',

    //所有的树形菜单，每个元素为顶部菜单，顶部菜单的子级（如果有）为侧边栏菜单
    menus: [],

    //主题色
    color: '#1890ff',
    //导航模式，'aside'、'aside-two-part'、'head'、'mix'
    navMode: 'mix'
}

const store = Vue.observable(state)

export const getters = createGetters(store)

export const mutations = {
    ...createMutations(store),

    menus(v) {
        sort(v)
        store.menus = v
    }
}

//菜单排序
function sort(routes) {
    if (!Array.isArray(routes) || routes.length === 0) {
        return
    }

    //菜单排序值的空值处理
    const getSortValue = item => {
        const sort = deepGetSortValue(item)
        return isEmpty(sort) ? 10000 : sort
    }

    //获取菜单的排序值
    const deepGetSortValue = item => {
        const {children = [], meta: {hidden, sort} = {}} = item

        if (hidden) return null

        if (!isEmpty(sort)) return sort

        //如果只有一个子节点，那么取子节点的排序值
        if (children.length === 1) {
            return deepGetSortValue(children[0])
        }

        return null
    }

    //对根节点排序
    routes.sort((pre, next) => {
        pre = getSortValue(pre)
        next = getSortValue(next)
        if (pre < next) return -1
        if (pre === next) return 0
        return 1
    })

    //对每一个根节点的子级排序
    routes.forEach(route => {
        const {children} = route
        children && children.length && sort(children)
    })
}

window.addEventListener('resize', debounce(() => {
    !document.hidden && mutations.isMobile(isMobile())
}))

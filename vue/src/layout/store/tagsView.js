import Vue from 'vue'
import {route as routeConfig} from "@/config"
import {mutations as iframeMutations} from "@/layout/store/iframe"
import {createGetters, createMutations} from "@/util/observable"

const state = {
    //显示的页签，{...route,title:route.meta.title}对象数组
    visitedViews: [],

    //缓存的页签，用于<keep-router-view-alive/>:include
    cachedViews: [],

    //路由过渡动画名称
    transitionName: routeConfig.animate.default
}

const store = Vue.observable(state)

function getCachedViewKey(view) {
    const {usePathKey, useFullPathKey} = view.meta || {}
    return usePathKey ? view.path : useFullPathKey ? view.fullPath : view.name
}

function addVisitedView(view) {
    const {title = '暂无标题'} = view.meta || {}

    if (store.visitedViews.some(v => v.path === view.path)) return

    store.visitedViews.push({...view, title})
}

function addCachedView(view) {
    const {noCache, iframe, usePathKey, useFullPathKey} = view.meta || {}

    if (noCache || iframe || !view.name && !usePathKey && !useFullPathKey) return

    const key = getCachedViewKey(view)

    if (store.cachedViews.includes(key)) return

    store.cachedViews.push(key)
}

function delVisitedView(view) {
    const index = store.visitedViews.findIndex(i => i.path === view.path)
    index > -1 && store.visitedViews.splice(index, 1)
}

function delCachedView(view) {
    const key = getCachedViewKey(view)
    const index = store.cachedViews.indexOf(key)
    index > -1 && store.cachedViews.splice(index, 1)
}

export const getters = createGetters(store)

export const mutations = {
    ...createMutations(store),

    addVisitedView,
    addCachedView,
    addView(view) {
        addVisitedView(view)
        addCachedView(view)
    },

    delVisitedView,
    delCachedView,
    delView(view) {
        delVisitedView(view)
        delCachedView(view)
        iframeMutations.del(view.meta ? view.meta.iframe : null)
    },
    delOthersViews(view) {
        const visitedViews = store.visitedViews.filter(v => v.meta.affix || v.path === view.path)
        const name = store.cachedViews.find(name => name === getCachedViewKey(view))

        store.visitedViews = visitedViews
        store.cachedViews = name ? [name] : []
    },
    delAllViews() {
        store.visitedViews = store.visitedViews.filter(tag => tag.meta && tag.meta.affix)
        store.cachedViews = []
    }
}

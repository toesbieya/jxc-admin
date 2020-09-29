/**
 * 多页签的响应式数据
 */
import Vue from 'vue'
import {route as routeConfig} from "@/config"
import {mutations as pageMutations} from "@/layout/store/page"
import {getRouterViewCacheKey} from "@/layout/util"
import {bindThis} from "@/util"
import {createGetters, createMutations} from "@/util/observable"

const state = {
    //是否启用
    enabled: true,
    //是否启用快捷键切换功能
    shortcut: true,
    //是否将页签持久化到sessionStorage
    persistent: true,

    //显示的页签，vue-router的routeConfig对象数组
    visitedViews: [],

    //缓存的页签，用于<keep-router-view-alive/>:include
    cachedViews: [],

    //路由过渡动画名称
    transitionName: routeConfig.animate.default
}

const store = Vue.observable(state)

export const getters = createGetters(store)

export const mutations = bindThis({
    ...createMutations(store),

    enabled(v) {
        store.enabled = v
        //多页签停用时清除所有页面缓存
        !v && this.delAllViews()
    },

    addVisitedView(view) {
        if (store.visitedViews.some(v => v.path === view.path)) return

        store.visitedViews.push(view)
    },
    addCachedView(view) {
        const {noCache, iframe, usePathKey, useFullPathKey} = view.meta || {}

        if (noCache || iframe || !view.name && !usePathKey && !useFullPathKey) return

        const key = getRouterViewCacheKey(view)

        if (store.cachedViews.includes(key)) return

        store.cachedViews.push(key)
    },
    addView(view) {
        this.addVisitedView(view)
        this.addCachedView(view)
    },

    delVisitedView(view) {
        const index = store.visitedViews.findIndex(i => i.path === view.path)
        index > -1 && store.visitedViews.splice(index, 1)
    },
    delCachedView(view) {
        const key = getRouterViewCacheKey(view)
        const index = store.cachedViews.indexOf(key)
        index > -1 && store.cachedViews.splice(index, 1)
    },
    delView(view) {
        this.delVisitedView(view)
        this.delCachedView(view)
        pageMutations.delIframe(view.meta ? view.meta.iframe : null)
    },
    delOthersViews(view) {
        const visitedViews = store.visitedViews.filter(v => v.meta.affix || v.path === view.path)
        const key = store.cachedViews.find(key => key === getRouterViewCacheKey(view))

        store.visitedViews = visitedViews
        store.cachedViews = key ? [key] : []
    },
    delAllViews() {
        store.visitedViews = store.visitedViews.filter(tag => tag.meta && tag.meta.affix)
        store.cachedViews = []
    }
})

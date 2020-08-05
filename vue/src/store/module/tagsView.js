import {createMutations} from "@/util"

const state = {
    //显示的页签，{...route,title:route.meta.title}对象数组
    visitedViews: [],

    //缓存的页签，用于<keep-router-view-alive/>:include
    cachedViews: [],

    //路由过渡动画名称
    transitionName: 'el-fade-in-linear'
}

const mutations = {
    ...createMutations(state),

    addVisitedView(state, view) {
        const {title = '暂无标题'} = view.meta || {}

        if (state.visitedViews.some(v => v.path === view.path)) return

        state.visitedViews.push({...view, title})
    },
    addCachedView(state, view) {
        const {noCache, iframe, isDetailPage} = view.meta || {}

        if (noCache || iframe || !view.name && !isDetailPage) return

        const key = getCachedViewKey(view)

        if (state.cachedViews.includes(key)) return

        state.cachedViews.push(key)
    },

    delVisitedView(state, view) {
        const index = state.visitedViews.findIndex(i => i.path === view.path)
        index > -1 && state.visitedViews.splice(index, 1)
    },
    delCachedView(state, view) {
        const key = getCachedViewKey(view)
        const index = state.cachedViews.indexOf(key)
        index > -1 && state.cachedViews.splice(index, 1)
    }
}

const actions = {
    addView({commit}, view) {
        commit('addVisitedView', view)
        commit('addCachedView', view)
    },
    delView({commit}, view) {
        commit('delVisitedView', view)
        commit('delCachedView', view)
        commit('iframe/del', view.meta ? view.meta.iframe : null, {root: true})
    },

    delOthersViews({state, commit}, view) {
        const visitedViews = state.visitedViews.filter(v => v.meta.affix || v.path === view.path)
        const name = state.cachedViews.find(name => name === getCachedViewKey(view))

        commit('visitedViews', visitedViews)
        commit('cachedViews', name ? [name] : [])
    },
    delAllViews({state, commit}) {
        commit('visitedViews', state.visitedViews.filter(tag => tag.meta && tag.meta.affix))
        commit('cachedViews', [])
    }
}

function getCachedViewKey(view) {
    const isDetailPage = view.meta && view.meta.isDetailPage
    return isDetailPage ? view.path : view.name
}

export default {
    namespaced: true,
    state,
    mutations,
    actions
}

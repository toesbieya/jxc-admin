import {rightSideRouteTransition} from '@/config'
import {createMutations} from "@/utils"

const state = {
    visitedViews: [],
    cachedViews: [],
    transitionName: rightSideRouteTransition
}

const mutations = {
    ...createMutations(state),

    addVisitedView(state, view) {
        if (state.visitedViews.some(v => v.path === view.path)) return
        state.visitedViews.push({...view, title: view.meta.title || 'no-name'})
    },
    addCachedView(state, view) {
        if (state.cachedViews.includes(view.name)) return
        if (!view.meta.noCache && !view.meta.iframe) {
            state.cachedViews.push(view.name)
        }
    },

    delVisitedView(state, view) {
        const index = state.visitedViews.findIndex(i => i.path === view.path)
        index > -1 && state.visitedViews.splice(index, 1)
    },
    delCachedView(state, view) {
        const index = state.cachedViews.indexOf(view.name)
        index > -1 && state.cachedViews.splice(index, 1)
    },

    updateVisitedViews(state, view) {
        for (let v of state.visitedViews) {
            if (v.path === view.path) {
                v = Object.assign(v, view)
                break
            }
        }
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
        const name = state.cachedViews.find(name => name === view.name)
        commit('visitedViews', visitedViews)
        commit('cachedViews', name ? [name] : [])
    },
    delAllViews({state, commit}) {
        commit('visitedViews', state.visitedViews.filter(tag => tag.meta.affix))
        commit('cachedViews', [])
    }
}

export default {
    namespaced: true,
    state,
    mutations,
    actions
}

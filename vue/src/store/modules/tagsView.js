import {rightSideRouteTransition} from '@/config'

const state = {
    visitedViews: [],
    cachedViews: [],
    transitionName: rightSideRouteTransition
}

const mutations = {
    SET_VISITED_VIEWS(state, views) {
        state.visitedViews = views
    },
    SET_CACHED_VIEWS(state, views) {
        state.cachedViews = views
    },

    ADD_VISITED_VIEW: (state, view) => {
        if (state.visitedViews.some(v => v.path === view.path)) return
        state.visitedViews.push({...view, title: view.meta.title || 'no-name'})
    },
    ADD_CACHED_VIEW: (state, view) => {
        if (state.cachedViews.includes(view.name)) return
        if (!view.meta.noCache && !view.meta.iframe) {
            state.cachedViews.push(view.name)
        }
    },

    DEL_VISITED_VIEW: (state, view) => {
        const index = state.visitedViews.findIndex(i => i.path === view.path)
        index > -1 && state.visitedViews.splice(index, 1)
    },
    DEL_CACHED_VIEW: (state, view) => {
        const index = state.cachedViews.indexOf(view.name)
        index > -1 && state.cachedViews.splice(index, 1)
    },

    DEL_OTHERS_VISITED_VIEWS: (state, view) => {
        state.visitedViews = state.visitedViews.filter(v => {
            return v.meta.affix || v.path === view.path
        })
    },
    DEL_OTHERS_CACHED_VIEWS: (state, view) => {
        const index = state.cachedViews.indexOf(view.name)
        if (index > -1) {
            state.cachedViews = state.cachedViews.slice(index, index + 1)
        }
        else state.cachedViews = []
    },

    UPDATE_VISITED_VIEW: (state, view) => {
        for (let v of state.visitedViews) {
            if (v.path === view.path) {
                v = Object.assign(v, view)
                break
            }
        }
    },

    SET_TRANSITION_NAME: (state, name) => {
        state.transitionName = name
    }
}

const actions = {
    addView({commit, dispatch}, view) {
        commit('ADD_VISITED_VIEW', view)
        commit('ADD_CACHED_VIEW', view)
    },
    delView({commit}, view) {
        commit('DEL_VISITED_VIEW', view)
        commit('DEL_CACHED_VIEW', view)
        commit('iframe/DEL_IFRAME', view.meta ? view.meta.iframe : null, {root: true})
    },

    delOthersViews({commit, state}, view) {
        commit('DEL_OTHERS_VISITED_VIEWS', view)
        commit('DEL_OTHERS_CACHED_VIEWS', view)
    },
    delAllViews({state, commit}) {
        commit('SET_VISITED_VIEWS', state.visitedViews.filter(tag => tag.meta.affix))
        commit('SET_CACHED_VIEWS', [])
    }
}

export default {
    namespaced: true,
    state,
    mutations,
    actions
}

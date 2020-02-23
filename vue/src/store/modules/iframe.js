import {createMutations} from "@/utils"

const state = {
    show: false,
    current: '',
    list: []
}

const mutations = {
    ...createMutations(state, false),
    ADD_IFRAME(state, src) {
        if (state.list.some(i => i === src)) return
        state.list.push(src)
    },
    DEL_IFRAME(state, src) {
        if (!src) return
        let index = state.list.findIndex(i => i === src)
        index > -1 && state.list.splice(index, 1)
    }
}

const actions = {
    open({commit}, src) {
        if (!src) return
        commit('SET_SHOW', true)
        commit('SET_CURRENT', src)
        commit('ADD_IFRAME', src)
    },
    close({commit}, src) {
        commit('SET_SHOW', false)
        commit('SET_CURRENT', '')
        commit('DEL_IFRAME', src)
    }
}

export default {
    namespaced: true,
    state,
    mutations,
    actions
}

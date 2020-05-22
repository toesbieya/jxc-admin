import {createMutations} from "@/utils"

const state = {
    show: false,
    current: '',
    list: []
}

const mutations = {
    ...createMutations(state),
    addIframe(state, src) {
        if (state.list.some(i => i === src)) return
        state.list.push(src)
    },
    delIframe(state, src) {
        if (!src) return
        let index = state.list.findIndex(i => i === src)
        index > -1 && state.list.splice(index, 1)
    }
}

const actions = {
    open({commit}, src) {
        if (!src) return
        commit('show', true)
        commit('current', src)
        commit('addIframe', src)
    },
    close({commit}, src) {
        commit('show', false)
        commit('current', '')
        commit('delIframe', src)
    }
}

export default {
    namespaced: true,
    state,
    mutations,
    actions
}

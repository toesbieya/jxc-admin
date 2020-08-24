import {createMutations} from "@/util"

const state = {
    show: false,
    current: '',
    list: []
}

const mutations = {
    ...createMutations(state),
    add(state, src) {
        !state.list.includes(src) && state.list.push(src)
    },
    del(state, src) {
        const index = state.list.findIndex(i => i === src)
        index > -1 && state.list.splice(index, 1)
    }
}

const actions = {
    open({commit}, {src}) {
        commit('show', true)
        commit('current', src)
        commit('add', src)
    },
    close({commit}, {src, del}) {
        commit('show', false)
        commit('current', '')
        del && commit('del', src)
    }
}

export default {
    namespaced: true,
    state,
    mutations,
    actions
}

import Vue from "vue"

const state = {
    map: {}
}

const mutations = {
    init(state, key) {
        if (state.map.hasOwnProperty(key)) {
            state.map[key] = false
        }
        else Vue.set(state.map, key, false)
    },

    emit(state, key) {
        state.map[key] = true
    },

    renew(state, key) {
        state.map[key] = false
    }
}

export default {
    namespaced: true,
    state,
    mutations
}

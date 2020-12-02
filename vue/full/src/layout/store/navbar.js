import Vue from 'vue'
import {createGetters, createMutations} from "@/util/observable"

const state = {
    //主题，light 或 dark
    theme: 'light',
}

const store = Vue.observable(state)

export const getters = createGetters(store)

export const mutations = createMutations(store)

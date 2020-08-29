import Vue from 'vue'
import {createGetters} from "@/util/observable"

const state = {
    show: false,
    current: '',
    list: []
}

const store = Vue.observable(state)

function add(src) {
    !store.list.includes(src) && store.list.push(src)
}

function del(src) {
    const index = store.list.findIndex(i => i === src)
    index > -1 && store.list.splice(index, 1)
}

function open({src}) {
    store.show = true
    store.current = src
    add(src)
}

function close({src, del: needDelete}) {
    store.show = false
    store.current = ''
    needDelete && del(src)
}

export const getters = createGetters(store)

export const mutations = {del, open, close}

import Vue from 'vue'

export const store = Vue.observable({
    data: []
})

export function init(url) {
    return fetch(url)
        .then(r => r.json())
        .then(r => store.data = r || [])
}

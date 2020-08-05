import Vue from 'vue'
import {regionDataUrl} from '@/config'

export const store = Vue.observable({
    data: []
})

export function init() {
    return fetch(regionDataUrl)
        .then(r => r.json())
        .then(r => store.data = r || [])
}
